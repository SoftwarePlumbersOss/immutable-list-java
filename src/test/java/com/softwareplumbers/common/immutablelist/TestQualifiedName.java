/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwareplumbers.common.immutablelist;

import java.util.Map;
import java.util.TreeMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author jonathan
 */
public class TestQualifiedName {

	@Test
	public void testApply() {
        // We are using maps instead of JsonObjects here because we don't want to create
        // a depenency on a json implementation.
        
        // m1 is equivalent to a Json object: { "a" : "A", "b" : "B" }
		Map<String,Object> m1 = new TreeMap<>(); m1.put("a", "A"); m1.put("b", "B");
        // m2 is equivalent to a Json object: { "c" : "C", "d" : "D" }
		Map<String,Object> m2 = new TreeMap<>(); m2.put("c", "C"); m2.put("d", "D");
        // m3 is equivalent to a Json object: { "x" : { "a" : "A", "b" : "B" }, y : { "c" : "C", "d" : "D" } }
		Map<String,Object> m3 = new TreeMap<>(); m3.put("x",m1); m3.put("y", m2);
        // QualifiedName.of("x","b") is x.b
		assertEquals("B", QualifiedName.of("x","b").apply(m3));
        // QualifiedName.of("x","b") is y.c
		assertEquals("C", QualifiedName.of("y","c").apply(m3));
		assertNull(QualifiedName.of("y","a").apply(m3));
		assertNull(QualifiedName.of("y","a","x").apply(m3));
	}

	@Test
	public void testPatternMatch() {
		QualifiedName shouldMatch1 = QualifiedName.of("peter","piper","picked");
		QualifiedName shouldMatch2 = QualifiedName.of("peter","poper","jumped");
		QualifiedName shouldntMatch = QualifiedName.of("david","piper","picked");
		QualifiedName pattern = QualifiedName.of("p.*","p.per",".*d");
		
		assertTrue(shouldMatch1.matches(pattern, true));
		assertTrue(shouldMatch2.matches(pattern, true));
		assertFalse(shouldntMatch.matches(pattern, true));
	}    
}
