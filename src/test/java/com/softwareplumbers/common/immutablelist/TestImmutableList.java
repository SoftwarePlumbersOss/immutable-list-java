package com.softwareplumbers.common.immutablelist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

import org.junit.Test;

public class TestImmutableList {
	
	@Test
	public void testDottedFormat() {
		assertEquals("a.b.c", ImmutableList.of("a").add("b").add("c").toString());
	}

	@Test
	public void testRootFormat() {
		assertEquals("", ImmutableList.empty().toString());
	}
	
	@Test
	public void testJoin() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals("a/b/c", ABC.join(Object::toString, "/"));
		ImmutableList<String> ABCDEF = ImmutableList.of("a","b","c","d","e","f");
		assertEquals("a/b/c/d/e/f", ABCDEF.join(Object::toString, "/"));
		ImmutableList<String> HORRIBLE = ImmutableList.of("a","/b","c","d/","e/j","f");
		assertEquals("a/\\/b/c/d\\//e\\/j/f", HORRIBLE.join(Object::toString, "/"));
		assertEquals("", ImmutableList.empty().join(Object::toString, "/"));
	}
    
    @Test 
    public void testJoinParseRoundtrip() {
		ImmutableList<String> HORRIBLE = ImmutableList.of("a","/b","c","d/","e/j","f");
        assertEquals(HORRIBLE, ImmutableList.parse(Function.identity(), HORRIBLE.join(Object::toString, "/"),"/"));
    }

	@Test
	public void testEquals() {
		ImmutableList<String> a = ImmutableList.of("a");
		ImmutableList<String> b = ImmutableList.of("b");
		ImmutableList<String> a2 = ImmutableList.of("a");
		ImmutableList<String> b2 = ImmutableList.of("b");
		ImmutableList<String> ab = ImmutableList.of("a").add("b");
		ImmutableList<String> ba = ImmutableList.of("b").add("a");
		
		assertTrue(ImmutableList.empty().equals(ImmutableList.empty()));
		assertTrue(a.equals(a2));
		assertTrue(b.equals(b2));
		assertFalse(ImmutableList.empty().equals(a));
		assertFalse(a.equals(ImmutableList.empty()));
		assertFalse(a.equals(b));
		assertFalse(a.equals(ab));
		assertFalse(a.equals(ba));
		assertFalse(ab.equals(ba));
		assertFalse(ab.equals(ImmutableList.empty()));
	}
	
	@Test
	public void testComparison() {
		ImmutableList<String> a = ImmutableList.of("a");
		ImmutableList<String> b = ImmutableList.of("b");
		ImmutableList<String> a2 = ImmutableList.of("a");
		ImmutableList<String> b2 = ImmutableList.of("b");
		ImmutableList<String> ab = ImmutableList.of("a").add("b");
		ImmutableList<String> ba = ImmutableList.of("b").add("a");
		
		assertEquals(0,ImmutableList.empty().compareTo(ImmutableList.empty()));
		assertEquals(0,a.compareTo(a2));
		assertEquals(0,b.compareTo(b2));
		assertEquals(-1, ((ImmutableList<String>)ImmutableList.empty()).compareTo(a));
		assertEquals(1, a.compareTo(ImmutableList.empty()));
		assertEquals(-1,a.compareTo(b));
		assertEquals(1,b.compareTo(a));
		assertEquals(-1,a.compareTo(ab));
		assertEquals(-1,a.compareTo(ba));
		assertEquals(-1,ab.compareTo(ba));
		assertEquals(1,ab.compareTo(ImmutableList.empty()));
	}
	
	@Test
	public void testParse() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals(ABC, ImmutableList.parse(Function.identity(), "a/b/c","/"));
		assertEquals(ABC, ImmutableList.parse(Function.identity(), "/a/b/c","/"));
		assertEquals(ABC, ImmutableList.parse(Function.identity(), "/a/b/c/","/"));
		ImmutableList<String> AC = ImmutableList.of("a/b","c");
		assertEquals(AC, ImmutableList.parse(Function.identity(), "a\\/b/c","/"));
	}
	
	@Test
	public void testReverse() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals(ImmutableList.of("c","b","a"), ABC.reverse());				
	}
	
	@Test
	public void testStartsWith() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertTrue(ABC.startsWith(ImmutableList.of("a","b","c")));
		assertTrue(ABC.startsWith(ImmutableList.of("a","b")));
		assertTrue(ABC.startsWith(ImmutableList.of("a")));
		assertFalse(ABC.startsWith(ImmutableList.of("c")));
		assertFalse(ABC.startsWith(ImmutableList.of("a","b","c","d")));
	}

	@Test
	public void testEndsWith() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertTrue(ABC.endsWith(ImmutableList.of("a","b","c")));
		assertTrue(ABC.endsWith(ImmutableList.of("b","c")));
		assertTrue(ABC.endsWith(ImmutableList.of("c")));
		assertFalse(ABC.endsWith(ImmutableList.of("a")));
		assertFalse(ABC.endsWith(ImmutableList.of("a","b","c","d")));
	}

	@Test
	public void testIndexFromEnd() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals(2,ABC.indexFromEnd(e -> e.equals("a")));
		assertEquals(1,ABC.indexFromEnd(e -> e.equals("b")));
		assertEquals(0,ABC.indexFromEnd(e -> e.equals("c")));
		assertEquals(-1,ABC.indexFromEnd(e -> e.equals("d")));
	}
	
	@Test
	public void testIndexOf() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals(0,ABC.indexOf(e -> e.equals("a")));
		assertEquals(1,ABC.indexOf(e -> e.equals("b")));
		assertEquals(2,ABC.indexOf(e -> e.equals("c")));
		assertEquals(-1,ABC.indexOf(e -> e.equals("d")));
	}

	@Test
	public void testGet() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals("a", ABC.get(0));
		assertEquals("b", ABC.get(1));
		assertEquals("c", ABC.get(2));
	}
	
	@Test
	public void testGetFromEnd() {
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals("a", ABC.getFromEnd(2));
		assertEquals("b", ABC.getFromEnd(1));
		assertEquals("c", ABC.getFromEnd(0));
	}
	
	@Test
	public void testUpTo() {
		ImmutableList<String> TEST1 = ImmutableList.of("www","softwareplumbers","com");
		ImmutableList<String> result1 = TEST1.upTo(elem -> elem.contains("ware"));
		ImmutableList<String> result2 = TEST1.upTo(elem -> elem.contains("wore"));
		assertEquals(ImmutableList.of("www","softwareplumbers"), result1);
		assertEquals(ImmutableList.empty(), result2);
	}
	
	@Test
	public void testUpToLast() {
		ImmutableList<Integer> TEST1 = ImmutableList.of(1,2,3,2,4,5);
		ImmutableList<Integer> result1 = TEST1.upTo(elem -> elem.equals(2));
		ImmutableList<Integer> result2 = TEST1.upToLast(elem -> elem.equals(2));
		ImmutableList<Integer> result3 = TEST1.upToLast(elem -> elem.equals(6));
		assertEquals(ImmutableList.of(1,2), result1);
		assertEquals(ImmutableList.of(1,2,3,2), result2);
        assertEquals(ImmutableList.empty(), result3);
	}
    
    @Test
	public void testFrom() {
		ImmutableList<String> TEST1 = ImmutableList.of("www","softwareplumbers","com");
		ImmutableList<String> result1 = TEST1.from(elem -> elem.contains("ware"));
		ImmutableList<String> result2 = TEST1.from(elem -> elem.contains("wore"));
		assertEquals(ImmutableList.of("com"), result1);
		assertEquals(TEST1, result2);
	}

	@Test
	public void testFromLast() {
		ImmutableList<Integer> TEST1 = ImmutableList.of(1,2,3,2,4,5);
		ImmutableList<Integer> result1 = TEST1.from(elem -> elem.equals(2));
		ImmutableList<Integer> result2 = TEST1.fromLast(elem -> elem.equals(2));
		ImmutableList<Integer> result3 = TEST1.fromLast(elem -> elem.equals(6));
		assertEquals(ImmutableList.of(3,2,4,5), result1);
		assertEquals(ImmutableList.of(4,5), result2);
		assertEquals(TEST1, result3);
	}    
    
	@Test
	public void testRight() {
		ImmutableList<String> ABCDEF = ImmutableList.of("a","b","c","d","e","f");
		ImmutableList<String> DEF = ImmutableList.of("d","e","f");
		assertEquals(DEF, ABCDEF.right(3));
		assertEquals(ImmutableList.empty(), ABCDEF.right(0));
		assertEquals(ABCDEF, ABCDEF.right(20));
	}
	
	@Test
	public void testLeftFromEnd() {
		ImmutableList<String> ABCDEF = ImmutableList.of("a","b","c","d","e","f");
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		assertEquals(ABC, ABCDEF.leftFromEnd(3));
		assertEquals(ImmutableList.empty(), ABCDEF.leftFromEnd(20));
		assertEquals(ABCDEF, ABCDEF.leftFromEnd(0));
	}
	
	@Test
	public void testLeft() {
		ImmutableList<String> ABCDEF = ImmutableList.of("a","b","c","d","e","f");
		ImmutableList<String> ABC = ImmutableList.of("a","b","c");
		ImmutableList<String> ABCD = ImmutableList.of("a","b","c","d");
		assertEquals(ABC, ABCDEF.left(3));
		assertEquals(ABCD, ABCDEF.left(4));
		assertEquals(ImmutableList.empty(), ABCDEF.left(0));
		assertEquals(ABCDEF, ABCDEF.left(20));
	}

	@Test
	public void testRightFromStart() {
		ImmutableList<String> ABCDEF = ImmutableList.of("a","b","c","d","e","f");
		ImmutableList<String> DEF = ImmutableList.of("d","e","f");
		ImmutableList<String> EF = ImmutableList.of("e","f");
		assertEquals(DEF, ABCDEF.rightFromStart(3));
		assertEquals(EF, ABCDEF.rightFromStart(4));
		assertEquals(ABCDEF, ABCDEF.rightFromStart(0));
		assertEquals(ImmutableList.empty(), ABCDEF.rightFromStart(20));
	}
		
    @Test
    public void testTransform() {
        ImmutableList<String> n1 = ImmutableList.of("x","abc","2");
        assertEquals(ImmutableList.of("X","ABC","2"), n1.transform(String::toUpperCase));
    }
    
    @Test
    public void testTransformWithLambda() {
        ImmutableList<String> n1 = ImmutableList.of("x","abc","2");
        assertEquals(ImmutableList.of("X","ABC","2"), n1.transform(i->i.toUpperCase()));
    }
    
    private static class TestException extends Exception {};
    
    private static String testTransform(String input) throws TestException {
        if ("ERROR".equals(input)) throw new TestException();
        return input;
    }
    
    @Test
    public void testTransformWithCheckedException() {
        try {
            ImmutableList<String> n1 = ImmutableList.of("x","abc","2");
            assertEquals(ImmutableList.of("x","abc","2"), n1.transform(TestImmutableList::testTransform));
        } catch (TestException e) {
            // OK
        }
    }
    
	@Test
	public void testFind() {
		ImmutableList<String> TEST1 = ImmutableList.of("one","two","three","four","five");
		Optional<String> result1 = TEST1.find(elem -> elem.startsWith("t"));
		Optional<String> result2 = TEST1.findLast(elem -> elem.startsWith("t"));
		Optional<String> result3 = TEST1.findLast(elem -> elem.startsWith("z"));
		assertEquals(Optional.of("two"), result1);
		assertEquals(Optional.of("three"), result2);
		assertEquals(Optional.empty(), result3);
	} 
}
