JavaCupLib
==========

A minimal fork of the Java [CUP parser
generator](http://www2.cs.tum.edu/projects/cup/) that can be used as a
parser-generator library.

## Goal

CUP is small and fast, but the code presents problems for reuse,
including these:

* Many classes such as `Main` and `emit` act as global variables,
  making the code thread-unsafe.

* Many decisions are hard-coded (e.g., the output stream is hardcoded
  to `System.err` in multiple places).

* The constructed LALR(1) machine is output either as human-readable
  text or encoded as arrays for the CUP runtime, neither of which is
  especially useful for reuse in other code.

The goal of JavaCupLib is to *minimally* modify the original CUP code
(refactoring globals, loosening access to allow overriding of behavior
in subclasses, introducing interfaces to allow substituting different
implementations) to allow reuse of the CUP logic by client code.

## Conformance to CUP

The code is derived from the tarfile downloaded from
http://www2.cs.tum.edu/projects/cup/releases/java-cup-src-11b-20140703.tar.gz

Aside from differences in timestamp and version information, the
behavior when calling `java_cup.Main.main` should be identical whether
calling CUP or the JavaCupLib jarfile.  We keep a minimially modified
version of the original `java-cup-11b.jar` in
`src/test/resources/JavaCupLib-str.jar` to test this invariant.

I don't currently have any plans to keep this library in synch with
CUP releases, but it should be fairly easy to do so.