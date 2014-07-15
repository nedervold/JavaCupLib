import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CompareDirectories {

	static class ExitException extends Exception {
		private static final long serialVersionUID = -3572195907878705007L;

		public final int code;

		public ExitException(final int code, final String message) {
			super(message);
			this.code = code;
		}
	}

	private static final int ARG_NOT_DIR = 1;
	private static final int BAD_ARG_COUNT = 2;
	private static final int DIR_CHILDREN_MISMATCH = 3;
	private static final int FILE_CONTENTS_MISMATCH = 4;
	private static final int FILE_SIZE_MISMATCH = 5;
	private static final int FILE_TYPE_MISMATCH = 6;
	private static final int UNCAUGHT_EXCEPTION = 7;

	private static void assertIsDir(final Path path) throws ExitException {
		if (!Files.isDirectory(path)) {
			exit(ARG_NOT_DIR, "argument " + path + " is not a directory");
		}
	}

	private static void compareDirs(final Path leftPath, final Path rightPath)
			throws IOException, ExitException {
		debugTrace("compareDirs", leftPath, rightPath);
		final List<Path> leftChildren = sortedPaths(leftPath);
		final List<Path> rightChildren = sortedPaths(rightPath);
		final List<Path> leftNames = fileNames(leftChildren);
		final List<Path> rightNames = fileNames(rightChildren);
		if (leftNames.equals(rightNames)) {
			debugTrace("namesEqual: " + leftChildren + ", " + rightChildren);
			for (int i = 0; i < leftNames.size(); i++) {
				compareEntries(leftChildren.get(i), rightChildren.get(i));
			}
		} else {
			final String msg = "children don't match: " + leftPath + " has "
					+ leftNames + " while " + rightPath + " has " + rightNames;
			exit(DIR_CHILDREN_MISMATCH, msg);
		}
	}

	private static void compareEntries(final Path leftPath, final Path rightPath)
			throws IOException, ExitException {
		debugTrace("compareEntries", leftPath, rightPath);
		final boolean leftIsDir = Files.isDirectory(leftPath);
		final boolean rightIsDir = Files.isDirectory(rightPath);
		if (leftIsDir == rightIsDir) {
			if (leftIsDir) {
				compareDirs(leftPath, rightPath);
			} else {
				compareFiles(leftPath, rightPath);
			}
		} else {
			final String msg = leftPath + " is a "
					+ (leftIsDir ? "folder" : "file") + " but " + rightPath
					+ " is not";
			exit(FILE_TYPE_MISMATCH, msg);
		}
	}

	private static void compareFiles(final Path leftPath, final Path rightPath)
			throws ExitException, IOException {
		debugTrace("compareFiles", leftPath, rightPath);
		final long leftSize = Files.size(leftPath);
		if (isJavaFile(leftPath)) {
			// sizes might not match due to time- or version-stamping
			compareJavaFiles(leftPath, rightPath);
		} else if (leftSize != Files.size(rightPath)) {
			exit(FILE_SIZE_MISMATCH, leftPath + " and " + rightPath
					+ " have different sizes");
		} else {
			compareBinaryFile(leftSize, leftPath, rightPath);
		}
	}

	private static void compareBinaryFile(final long size, final Path leftPath,
			final Path rightPath) throws IOException, ExitException {
		debugTrace("compareBinaryFile(" + size + ", " + leftPath + ", "
				+ rightPath + ")");
		try (BufferedInputStream leftBIS = new BufferedInputStream(
				new FileInputStream(leftPath.toFile()));
				BufferedInputStream rightBIS = new BufferedInputStream(
						new FileInputStream(rightPath.toFile()))) {
			final int BLOCK_SIZE = 1024;
			byte[] leftBlock = new byte[BLOCK_SIZE];
			byte[] rightBlock = new byte[BLOCK_SIZE];
			long remaining = size;
			while (remaining > 0) {
				long toRead = Math.min(BLOCK_SIZE, remaining);
				compareBinaryBlocks(leftPath, rightPath, leftBIS, rightBIS,
						leftBlock, rightBlock, toRead, size - remaining);
				remaining -= toRead;
			}
		}
	}

	private static void compareBinaryBlocks(Path leftPath, Path rightPath,
			BufferedInputStream leftBIS, BufferedInputStream rightBIS,
			byte[] leftBlock, byte[] rightBlock, long toRead, long offset)
			throws ExitException, IOException {
		debugTrace("compareBinaryBlocks(" + leftPath + ", " + rightPath
				+ ", toRead=" + toRead + ", offset=" + offset + ")");
		readBlock(leftBIS, leftBlock, toRead);
		readBlock(rightBIS, rightBlock, toRead);
		for (int i = 0; i < leftBlock.length; i++) {
			if (leftBlock[i] != rightBlock[i]) {
				exit(FILE_CONTENTS_MISMATCH, leftPath + " and " + rightPath
						+ " differ at byte " + (offset + i));
			}
		}
	}

	private static void readBlock(InputStream is, byte[] block, long toRead)
			throws IOException {
		int count = is.read(block);
		assert count == toRead;
	}

	private static void compareJavaFiles(final Path leftPath,
			final Path rightPath) throws IOException, ExitException {
		try (BufferedReader leftBR = new BufferedReader(new FileReader(
				leftPath.toFile()));
				BufferedReader rightBR = new BufferedReader(new FileReader(
						rightPath.toFile()))) {
			String leftLine, rightLine;
			boolean prevLineGenerator = false;
			int lineCount = 0;
			while (true) {
				leftLine = leftBR.readLine();
				rightLine = rightBR.readLine();
				lineCount++;
				boolean leftEOF = leftLine == null;
				boolean rightEOF = leftLine == null;
				if (leftEOF != rightEOF) {
					exit(FILE_CONTENTS_MISMATCH, leftPath + " and " + rightPath
							+ " differ at line " + lineCount);
				} else if (leftEOF) {
					return; // both done
				} else {
					if (prevLineGenerator) {
						// skip this line
						prevLineGenerator = false;
					} else if (isGeneratorLine(leftLine)) {
						// skip this too
						prevLineGenerator = true;
					} else if (!leftLine.equals(rightLine)) {
						exit(FILE_CONTENTS_MISMATCH, leftPath + " and "
								+ rightPath + " differ at line " + lineCount);
					}
				}
			}
		}
	}

	private static boolean isGeneratorLine(String line) {
		return line.startsWith("// The following code was generated by CUP")
				|| (line.startsWith("/** CUP") && line
						.contains("generated parser"));
	}

	private static void debugTrace(final String msg) {
		// System.err.println(msg);
	}

	private static void debugTrace(final String functionName,
			final Path leftPath, final Path rightPath) {
		debugTrace(functionName + "(" + leftPath + ", " + rightPath + ")");
	}

	private static void exit(final int code, final String msg)
			throws ExitException {
		throw new ExitException(code, msg);
	}

	private static List<Path> fileNames(final List<Path> paths) {
		final List<Path> result = new ArrayList<Path>();
		for (final Path path : paths) {
			result.add(path.getFileName());
		}
		return result;
	}

	private static boolean isJavaFile(final Path path) {
		final boolean res = path.toString().endsWith(".java");
		debugTrace(path + " isJavaFile? " + res);
		return res;
	}

	public static void main(final String[] args) {
		try {
			if (args.length == 2) {
				final Path leftPath = mkPath(args[0]);
				assertIsDir(leftPath);
				final Path rightPath = mkPath(args[1]);
				assertIsDir(rightPath);
				compareDirs(leftPath, rightPath);
			} else {
				exit(BAD_ARG_COUNT, "needs two arguments, not " + args.length);
			}
		} catch (final ExitException cde) {
			System.err.println(cde.getMessage());
			System.exit(cde.code);
		} catch (final Exception e) {
			System.err.println(e.getMessage());
			System.exit(UNCAUGHT_EXCEPTION);
		}
		// System.out.println("Directories are equal.");
	}

	private static Path mkPath(final String str) {
		return Paths.get(str);
	}

	private static List<Path> sortedPaths(final Path dir) throws IOException {
		final List<Path> result = new ArrayList<>();
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
			for (final Path entry : stream) {
				result.add(entry);
			}
		}
		Collections.sort(result);
		return result;
	}

}
