/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.softwareplumbers.common.immutablelist;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/** A specialization of ImmutableList - a Qualified Name.
 * 
 * Got tired of other implementations (e.g. Path) that have too many special-purpose 
 * methods in them.
 * 
 * Create a new Qualified name with QualifiedName.of("abc"). 
 * Add new parts to name with 'add'. QualifiedName.of("abc").add("def") etc.
 * 
 * @author Jonathan Essex
 */
public class QualifiedName extends AbstractImmutableList<String, QualifiedName> {

    private QualifiedName(QualifiedName parent, String part) { super (parent, part); }
    
    public static final QualifiedName ROOT = new QualifiedName(null,null) {
        @Override
        public boolean isEmpty() { return true; }
    };
    
	/** Preferred way to construct a new list 
	 * 
	 * Equivalent to emtpy().add(part)
	 * 
     * @param <T> Value type of list
	 * @param part base of new list
	 * @return a new list
	 */
	public static QualifiedName of(String part) {
		return ROOT.add(part);
	}
		
	/** Preferred way to construct a new list 
	 * 
	 * Equivalent to empty().add(parts)
	 * 
     * @param <T> Value type of list
	 * @param parts base of new list
	 * @return a new list
	 */
	public static <T extends Comparable<T>> QualifiedName of(String... parts) {
		return ROOT.addAll(parts);
	}
	
    /** Parse a string into an ImmutableList using the given separator.
     * 
     * @param list String to parse
     * @param separator Separator string 
     * @return A list consisting of elements of the given string, split by the given separator
     */
	public static QualifiedName parse(String list, String separator) {
		return ROOT.addParsed(Function.identity(), list, separator);
	}    

    @Override
    public QualifiedName getEmpty() {
        return ROOT;
    }

    @Override
    public QualifiedName add(String part) {
        return new QualifiedName(this, part);
    }
    
    public String join(String separator) {
        return join(Function.identity(), separator);
    }

	/** Match against a sequence of regular expressions
	 * 
	 * @param pattern A qualified name formed of regular expressions
	 * @param match_all matching flag
	 * @return true if regex parts from pattern match parts of this name 
	 */
	public boolean matches(QualifiedName pattern, boolean match_all) {
		return pattern.matches(this, (regex, myPart) -> Pattern.matches(regex, myPart), match_all);
	}

	/** Apply a qualified name to a map-of-maps (such as JsonObject)
     * @param <T> value type of map
     * @param map map of strings to T
     * @return the result of looking up successive elements of this name in map and returned maps. 
     */
	public <T> T apply(Map<String,T> map) {
		if (!parent.isEmpty()) {
			try { 
				map = (Map<String,T>)parent.apply(map);
			} catch (ClassCastException e) {
				map = null;
			}
		}
		return map == null ? null : map.get(part);
	}    
}
