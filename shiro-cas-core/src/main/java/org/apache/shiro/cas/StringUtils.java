/*
 * Copyright (c) 2017, hiwepy (https://github.com/hiwepy).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.shiro.cas;

/**
 * <p>Utility methods for String.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class StringUtils {

	/**
	 * Any number of these characters are considered delimiters between multiple
	 * context config paths in a single String value.
	 */
	public static String CONFIG_LOCATION_DELIMITERS = ",; \t\n";

	/**
	 * tokenize To String Array.
	 *
	 * @param str the str
	 * @return the result
	 */
	public static String[] tokenizeToStringArray(String str) {
		return tokenizeToStringArray(str, CONFIG_LOCATION_DELIMITERS);
	}

	/**
	 * Checks whether the given string has text.
	 *
	 * @param str the str
	 * @return the result
	 */
	public static boolean hasText(String str) {
		return str != null && !str.trim().isEmpty();
	}

	/**
	 * Checks whether the given string is empty.
	 *
	 * @param str the str
	 * @return the result
	 */
	public static boolean isEmpty(CharSequence str) {
		return str == null || str.length() == 0;
	}

	/**
	 * Compares two strings for equality.
	 *
	 * @param a the a
	 * @param b the b
	 * @return the result
	 */
	public static boolean equals(CharSequence a, CharSequence b) {
		return java.util.Objects.equals(a, b);
	}

	/**
	 * tokenize To String Array.
	 *
	 * @param str the str
	 * @param delimiters the delimiters
	 * @return the result
	 */
	public static String[] tokenizeToStringArray(String str, String delimiters) {
		if (str == null) {
			return new String[0];
		}
		java.util.StringTokenizer st = new java.util.StringTokenizer(str, delimiters);
		java.util.List<String> tokens = new java.util.ArrayList<>();
		while (st.hasMoreTokens()) {
			String token = st.nextToken().trim();
			if (!token.isEmpty()) {
				tokens.add(token);
			}
		}
		return tokens.toArray(new String[0]);
	}

}
