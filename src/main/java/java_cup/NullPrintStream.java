package java_cup;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class NullPrintStream extends PrintStream {
	static class NullOutputStream extends OutputStream {

		@Override
		public void write(int b) throws IOException {
			// Do nothing
		}

	}

	public NullPrintStream() {
		super(new NullOutputStream());
	}
}
