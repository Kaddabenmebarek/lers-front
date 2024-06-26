/*
 * @author Kadda
 */

package org.research.kadda.labinventory.core.utils;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.research.kadda.labinventory.data.SynthesisLibraryOrderDto;


/**
 * Different utility classes, which have no obvious other places
 *
 * @author Joel Freyss
 *
 */
@SuppressWarnings("unchecked")
public class MiscUtils {

	public static final String SPLIT_SEPARATORS = ",;\n\t";
	public static final String SPLIT_SEPARATORS_WITH_SPACE = SPLIT_SEPARATORS + " ";

	private static final DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

	private static final Date parseDate(String s) {
		try {
			return s == null ? null : dateFormat.parse(s);
		} catch (Exception e) {
			return null;
		}
	}

	public static String extractCommonPrefix(Collection<String> strings) {
		if(strings==null || strings.size()==0) return "";
		if(strings.size()==1) return strings.iterator().next();

		String res = null;
		for (String string : strings) {
			if(string==null || string.length()==0) continue;
			if(res==null) {
				res = string;
			} else {
				int commonLen = 0;
				while(commonLen < res.length() && commonLen<string.length() && res.charAt(commonLen)==string.charAt(commonLen)) {
					commonLen++;
				}
				res = res.substring(0, commonLen);
			}
		}
		//Remove the final '/' characters
		if(res!=null && res.length()>0 && res.charAt(res.length()-1)=='/') {
			res = res.substring(0, res.length()-1);
		}

		return res;
	}

	public static String extractCommonSuffix(Collection<String> strings) {
		if(strings==null || strings.size()==0) return "";
		if(strings.size()==1) return strings.iterator().next();

		String res = null;
		for (String string : strings) {
			if(string==null || string.length()==0) continue;
			if(res==null) {
				res = string;
			} else {
				int commonLen = 0;
				while(commonLen < res.length() && commonLen<string.length() && res.charAt(res.length()-1-commonLen)==string.charAt(string.length()-1-commonLen)) {
					commonLen++;
				}
				res = res.substring(res.length()-commonLen);
			}
		}

		return res;
	}

	public static String extractModifier(String s) {
		String[] modifiers = new String[] {"<", "<=", ">=", ">", "="};
		s = s.trim();
		for (String modifier : modifiers) {
			if(s.startsWith(modifier)) return modifier;
		}
		return "=";
	}

	public static String extractText(String s) {
		String modifier = extractModifier(s);
		if(!s.startsWith(modifier)) return s;
		return s.substring(modifier.length()).trim();
	}

	public static Date extractDate(String s) {
		String text = extractText(s);
		return parseDate(text);
	}

	public static Date addDays(Date d, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return cal.getTime();
	}


	public static String removeHtml(String s) {
		if(s==null) s = "";
		return s.replaceAll("<br>", "\n")
				.replaceAll("&nbsp;"," ")
				.replaceAll("\\<([^=>]*(=\\'.*?\\')?(=\\\".*?\\\")?)+>","")
				//				.replaceAll("\\<.*?>","")
				.replaceAll("\t", " ")
				.replaceAll("[\r\n]+", "\n")
				.replaceAll("[ ]+", " ").trim();
	}
	public static String removeHtmlAndNewLines(String s) {
		if(s==null) s = "";
		s = removeHtml(s).replace("\n", " ").replace("\"", "");
		if(s.length()==0) s = "";
		return s;
	}

	public static String convertLabel(String s) {
		if(s==null) s = "";
		s = s
				.replaceAll("<B>(.*?)\n", "<span style='font-weight:bold'>$1</span>\n")
				.replaceAll("<I>(.*?)\n", "<i>$1</i>\n")
				.replaceAll("<m>(.*?)\n", "<span style='color:880088'>$1</span>\n")
				.replaceAll("<b>(.*?)\n", "<span style='color:0000CC'>$1</span>\n")
				.replaceAll("<c>(.*?)\n", "<span style='color:004466'>$1</span>\n")
				.replaceAll("<y>(.*?)\n", "<span style='color:334400'>$1</span>\n")
				.replaceAll("<r>(.*?)\n", "<span style='color:CC0000'>$1</span>\n")
				.replaceAll("<g>(.*?)\n", "<span style='color:666666'>$1</span>\n")
				.replace("\n", " ")
				.replace("\"", "");
		if(s.length()==0) s = "-";
		return s;
	}

	/**
	 * Extract the first group of subsequent digits in the input string
	 * (Ex 124B45 will return 124)
	 * @param s
	 * @return
	 */
	public static String extractStartDigits(String s) {
		if(s==null) return null;
		Matcher m = Pattern.compile("^[0-9]*").matcher(s);
		if(m.find()) return m.group();
		else return "";
	}

	public static String flatten(Object[] strings) {
		return flatten(strings, ", ");
	}
	public static String flatten(Object[] strings, String separator ) {
		return flatten(Arrays.asList(strings), separator);
	}
	public static String flatten(Collection<?> strings) {
		return flatten(strings, ", ");
	}
	public static String flatten(Collection<?> strings, String separator ) {
		if(strings==null) return "";
		StringBuilder sb = new StringBuilder();
		for (Object s : strings) {
			if(s==null || s.toString().length()==0) continue;
			sb.append((sb.length()>0? separator: "") + s );
		}
		return sb.toString();
	}

	/**
	 * Format the Map like "key1=value1; key2=value2"
	 * @param map
	 * @return
	 */
	public static String flatten(Map<?, ?> map) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> e : map.entrySet()) {
			sb.append((sb.length()>0?"; ":"") + (e.getKey()==null?"":e.getKey()) + "=" + (e.getValue()==null?"":e.getValue()));
		}

		return sb.toString();
	}

	public static String[] cutText(String text, int maxLength) {
		if(text==null) return new String[0];

		List<String> res = new ArrayList<String>();
		int offset = 0;
		int minLength = Math.max(2, maxLength/3);
		while(offset<text.length()) {
			int index = Math.min(text.length()-1, offset+maxLength-1);
			for (; index >= offset + minLength; index--) {
				char c = text.charAt(index);
				if(c==' ' || c=='-') {break;}
				//				if(Character.isUpperCase(c)) {break;}
			}
			if(index<=offset+minLength) index = Math.min(text.length(), offset+maxLength);
			res.add(text.substring(offset, index).trim());
			offset = index;
		}
		return res.toArray(new String[res.size()]);

	}


	public static String unsplit(String[] strings) {
		return unsplit(strings, ", ");
	}

	public static String unsplit(String[] strings, String separator) {
		if(strings==null) return "";
		StringBuilder sb = new StringBuilder();
		for (String s : strings) {
			if(s==null || s.toString().length()==0) continue;
			if(s.indexOf(separator)>=0) {
				s = "\"" + s + "\"";
			}
			sb.append((sb.length()>0?separator:"")+ s );
		}
		return sb.toString();
	}

	public static List<Integer> splitIntegers(String s) {
		List<Integer> res = new ArrayList<Integer>();
		for(String string: split(s, SPLIT_SEPARATORS+" ")) {
			try {
				res.add(Integer.parseInt(string));
			} catch(Exception e) {
				throw new IllegalArgumentException(string+" is not an integer");
			}
		}
		return res;
	}

	public static List<Long> splitLong(String s) {
		List<Long> res = new ArrayList<Long>();
		for(String string: split(s, SPLIT_SEPARATORS+" ")) {
			try {
				res.add(Long.parseLong(string));
			} catch(Exception e) {
				throw new IllegalArgumentException(string+" is not an integer");
			}
		}
		return res;
	}

	/**
	 * Split a string, using the standard separators (,;\n\t)
	 * Unlike String.split, the string can contain the separator but they must be escaped with backslash
	 *
	 * The opposite function is unsplit.
	 *
	 * @param s
	 * @return
	 */
	public static String[] split(String s) {
		return split(s, SPLIT_SEPARATORS);
	}

	/**
	 * Split a string, using the given separators.
	 * Unlike String.split, the string can contain the separator but they must be escaped with backslash
	 *
	 * The opposite function is unsplit.
	 *
	 * Ex: split(" ",quoted"\,escaped\\,ok ", ",") will return those 2 elements [",quoted",escaped\],[ok].
	 *
	 *
	 * @param s
	 * @param separators
	 * @return
	 */
	public static String[] split(String s, String separators) {
		if(s==null) return new String[0];
		StringTokenizer st = new StringTokenizer(s, "\"" + separators, true);
		List<String> res = new ArrayList<String>();
		boolean inQuote = false;
		StringBuilder sb = new StringBuilder();
		while(st.hasMoreTokens()) {
			String token = st.nextToken();
			if(token.equals("\"")) {
				if(inQuote) {
					if(sb.toString().trim().length()>0) res.add(sb.toString().trim());
					sb.setLength(0);
					inQuote = false;
				} else {
					inQuote = true;
				}
			} else if(!inQuote && separators.indexOf(token)>=0) {
				if(sb.toString().trim().length()>0) res.add(sb.toString().trim());
				sb.setLength(0);
			} else {
				sb.append(token);
			}
		}
		if(sb.toString().trim().length()>0) res.add(sb.toString().trim());

		return res.toArray(new String[res.size()]);
	}




	/**
	 * Split a query by And / Or keywords.
	 *
	 * @return always an odd number of items: ex: "<=5", "AND", ">2"
	 */
	public static String[] splitByOrAnd(String string){
		String s[] = split(string, " ");
		List<String> res = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length; i++) {
			if(s[i].equalsIgnoreCase("AND") || s[i].equalsIgnoreCase("OR")) {
				if(sb.length()>0 && (res.size()%2)==0) res.add(sb.toString());
				if(res.size()%2==1) res.add(s[i]);
				sb.setLength(0);
			} else {
				sb.append((sb.length()>0?" ":"") + s[i]);
			}
		}
		if(sb.length()>0 && (res.size()%2)==0) {
			res.add(sb.toString());
		} else if(res.size()%2==0 && res.size()>0) {
			res.remove(res.size()-1);
		}

		return res.toArray(new String[res.size()]);
	}

	/**
	 * Extract n items from the list, if the n>list.size, return all.
	 * Otherwise return n elements using a progressive incrementation
	 * @param biosamples
	 * @param size
	 * @return
	 */
	public static<T> List<T> subList(List<T> list, int size) {
		if(size>=list.size()) return list;

		List<T> res = new ArrayList<T>();
		//We choose alpha such as sum(1+(alpha*i), i, 0, n-1)) = list.size
		int alpha = -2*(size-list.size()-1)/(size*(size-1));
		int index = 0;
		for(int i=0; i<size; i++) {
			if(index>=list.size()) return res;
			res.add(list.get(index));

			index += 1 + i * alpha;
		}
		return res;
	}

	public static String concatenate(String[][] rows, boolean removeSpecialCharacters) {
		StringBuilder sb = new StringBuilder();
		for (String[] strings : rows) {
			for (int i = 0; i < strings.length; i++) {
				if(i>0) sb.append("\t");
				String s = strings[i];
				if(s==null) s = "";
				if(removeSpecialCharacters) s = s.replaceAll("\t|\n", " ");
				sb.append(s);
			}
			sb.append(System.getProperty("line.separator"));
		}

		return sb.toString();
	}

	public static Integer parseInt(String integer) {
		try {
			return Integer.parseInt(integer.trim());
		} catch(Exception e) {
			return null;
		}
	}

	public static Double parseDouble(String doub) {
		try {
			return Double.parseDouble(doub.trim());
		} catch(Exception e) {
			return null;
		}
	}

	/**
	 * Serializes a Map<Integer,String> like: 1->joel, 2->to;=to, 3->null to 1=joel;2=to\;=to;3=
	 * All characters must be accepted. Returned string is [[id=string][#id=string]*]
	 * @param map
	 * @return
	 */
	public static String serializeIntegerMap(Map<Integer, String> map) {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, String> entry : map.entrySet()) {
			if(entry.getKey()<0) throw new RuntimeException("Cannot serialize: "+map);
			if(sb.length()>0) sb.append(";");
			sb.append(entry.getKey());
			sb.append("=");
			String s = entry.getValue();
			if(s!=null && s.length()>0) {
				if(s.contains("\\")) s = s.replace("\\", "\\\\");
				if(s.contains(";")) s = s.replace(";", "\\;");
				if(s.contains("\t")) s = s.replace("\t", "\\\t");
				sb.append(s);
			}
		}
		return sb.toString();
	}

	/**
	 * Inverse function of serializeIntegerMap
	 * @param data
	 * @return
	 */
	public static Map<Integer, String> deserializeIntegerMap(String data) {
		Map<Integer, String> map = new LinkedHashMap<>();
		if(data==null) return map;

		//Automat algorithm to parse data
		boolean inKey = true;
		int id = 0;
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if(inKey) {
				if(c=='=') {
					inKey = false;
				} else {
					if(c<'0' || c>'9') throw new RuntimeException("Cannot deserialize: "+data);
					id = id*10 + (c-'0');
				}
			} else {
				if(c=='\\') { //Escape character
					i++;
					if(i>=data.length()) throw new RuntimeException("Cannot deserialize: "+data);
					value.append(data.charAt(i));
				} else if(c==';' || c=='\t') {
					inKey = true;
					map.put(id, value.toString());
					id = 0;
					value.setLength(0);
				} else {
					value.append(c);
				}
			}
		}
		if(id>0) {
			map.put(id, value.toString());
		}
		return map;
	}

	/**
	 * Serializes a Map<Integer,String> like: 1->joel, 2->to;=to, 3->null to 1=joel;2=to\;=to;3=
	 * All characters must be accepted. Returned string is [[id=string][;id=string]*]
	 * @param map
	 * @return
	 */
	public static String serializeStringMap(Map<String, String> map) {
		if(map==null) return "";
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : map.entrySet()) {
			if(sb.length()>0) sb.append(";");
			sb.append(entry.getKey().replace("\\", "\\\\").replace(";", "\\;").replace("\t", "\\\t").replace("=", "\\="));
			sb.append("=");
			String s = entry.getValue();
			if(s!=null && s.length()>0) {
				sb.append(s.replace("\\", "\\\\").replace(";", "\\;").replace("\t", "\\\t").replace("=", "\\="));
			}
		}
		return sb.toString();
	}

	/**
	 * Inverse function of serializeIntegerMap
	 * @param data
	 * @return
	 */
	public static Map<String, String> deserializeStringMap(String data) {
		Map<String, String> map = new LinkedHashMap<>();
		if(data==null) return map;

		//Automat algorithm to parse data
		boolean inKey = true;
		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if(inKey) {
				if(c=='\\') { //Escape character
					i++;
					if(i>=data.length()) throw new RuntimeException("Cannot deserialize: "+data);
					key.append(data.charAt(i));
				} else if(c=='=') {
					inKey = false;
				} else {
					key.append(c);
				}
			} else {
				if(c=='\\') { //Escape character
					i++;
					if(i>=data.length()) throw new RuntimeException("Cannot deserialize: "+data);
					value.append(data.charAt(i));
				} else if(c==';' || c=='\t') {
					inKey = true;
					map.put(key.toString(), value.toString());
					key.setLength(0);
					value.setLength(0);
				} else {
					value.append(c);
				}
			}
		}
		if(!inKey) {
			map.put(key.toString(), value.toString());
		}
		return map;
	}


	/**
	 * Serializes a List<String> like: {joel, 123} to joel;123
	 * All characters must be accepted. Returned string is [[id=string][#id=string]*]
	 * @param map
	 * @return
	 */
	public static String serializeStrings(Collection<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			if(s==null) s = "";
			if(s.contains("\\")) s = s.replace("\\", "\\\\");
			if(s.contains(";")) s = s.replace(";", "\\;");
			if(s.contains("\t")) s = s.replace("\t", "\\\t");

			if(sb.length()>0) sb.append(";");
			sb.append(s);
		}
		return sb.toString();
	}

	/**
	 * Inverse function of serializeStrings
	 * @param data
	 * @return
	 */
	public static List<String> deserializeStrings(String data) {
		List<String> list = new ArrayList<>();
		if(data==null) return list;

		//Automat algorithm to parse data
		StringBuilder value = new StringBuilder();
		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if(c=='\\') { //Escape character
				i++;
				if(i>=data.length()) throw new RuntimeException("Cannot deserialize: "+data);
				value.append(data.charAt(i));
			} else if(c==';' || c=='\t') {
				list.add(value.toString());
				value.setLength(0);
			} else {
				value.append(c);
			}
		}
		list.add(value.toString());
		return list;
	}



	/**
	 * 1 -> 2
	 * 1. -> 2.
	 * 1C -> 1D
	 * 1C1 -> 1C2
	 * 1C9 -> 1C10
	 * A->B
	 * Z->??
	 * @param name
	 * @return
	 */
	public static String incrementName(String name) {
		String abbr = name;
		String suffix = "";
		for (int i = abbr.length()-1; i > 0 ; i--) {
			if(!Character.isDigit(abbr.charAt(i)) && !Character.isLetter(abbr.charAt(i))) {
				suffix = abbr.charAt(i) + suffix;
			} else {
				break;
			}
		}
		abbr = abbr.substring(0, abbr.length()-suffix.length());
		if(abbr==null || abbr.length()==0) return "1" + suffix;

		if(abbr.length()>0 && Character.isDigit(abbr.charAt(abbr.length()-1))) {
			//increment number
			int index;
			for(index=abbr.length()-1; index>=0 && Character.isDigit(abbr.charAt(index)); index--) {}
			index++;

			String number = abbr.substring(index);
			return abbr.substring(0, index) + (Integer.parseInt(number)+1) + suffix;
		} else if(abbr.length()>0 && Character.isLetter(abbr.charAt(abbr.length()-1))) {
			//increment letter
			int index;
			for(index=abbr.length()-1; index>=0 && Character.isLetter(abbr.charAt(index)); index--) {}
			index++;
			String letter = abbr.substring(index);
			if(letter.endsWith("Z") || letter.endsWith("z")) return abbr.substring(0, index) + "?" + suffix;
			return abbr.substring(0, index) + letter.substring(0, letter.length()-1) + (char)(letter.charAt(letter.length()-1)+1) + suffix;
		}
		return "??";
	}

	/**
	 * Returns true if obj is contained within array
	 */
	public static<T> boolean contains(T[] array, T obj) {
		if(obj==null) return false;
		for (T t : array) {
			if(obj.equals(t)) return true;
		}
		return false;
	}

	/**
	 * Returns true if one of the objects is contained within array
	 */
	public static<T> boolean contains(T[] array, Collection<T> objects) {
		for(T obj: objects) {
			if(obj==null) return false;
			for (T t : array) {
				if(obj.equals(t)) return true;
			}
		}
		return false;
	}

	public static void removeNulls(Collection<?> collection) {
		for (Iterator<?> iterator = collection.iterator(); iterator.hasNext();) {
			if(iterator.next()==null) iterator.remove();
		}
	}

	/**
	 * Some quick tests
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(charArray(500));
		System.out.println(convertLabel("<B>Bold\n<c>Cyan\nTest"));

		System.out.println(extractStartDigits("F5A78"));
		System.out.println(extractStartDigits("45F5A78"));
		System.out.println(Arrays.toString(cutText("Weighing Test", 12)));
		System.out.println(Arrays.toString(cutText("WeighingTest", 12)));
		System.out.println(Arrays.toString(cutText("Weighing-Test", 12)));
		System.out.println(Arrays.toString(cutText("WeightIncrease", 12)));
		System.out.println(Arrays.toString(cutText("FoodAndWater Intake", 12)));
		System.out.println(Arrays.toString(cutText("Longestwithoutspaces", 12)));

		System.out.println(Arrays.toString(splitByOrAnd("<3 and >1")));
		System.out.println(Arrays.toString(splitByOrAnd("<3 and or >1")));
		System.out.println(Arrays.toString(splitByOrAnd("<3 <2 and or >1")));

		String s = "2=two;3=third;4=special\\;;5=end";
		System.out.println();
		System.out.println(s);
		System.out.println(deserializeIntegerMap(s));
		System.out.println(serializeIntegerMap(deserializeIntegerMap(s)));
		assert s.equals(serializeIntegerMap(deserializeIntegerMap(s)));

		String s2 = "2=two\t3=third\t4=special\\;\t5=end";
		System.out.println();
		System.out.println(s2);
		System.out.println(deserializeIntegerMap(s2));
		System.out.println(serializeIntegerMap(deserializeIntegerMap(s2)));
		assert s.equals(serializeIntegerMap(deserializeIntegerMap(s2)));

		System.out.println();
		System.out.println(incrementName(""));
		System.out.println(incrementName("1"));
		System.out.println(incrementName("1."));
		System.out.println(incrementName("_9_"));
		System.out.println(incrementName("A"));
		System.out.println(incrementName("1A"));
		System.out.println(incrementName("1A1"));
		System.out.println(incrementName("1A19"));
		System.out.println(incrementName("{1d}"));
		System.out.println(incrementName("{1z}"));
		assert incrementName("").equals("1");
		assert incrementName("1").equals("2");
		assert incrementName("_9_").equals("_10_");
		assert incrementName("1.").equals("2.");
		assert incrementName("A").equals("B");
		assert incrementName("1A").equals("1B");
		assert incrementName("1A1").equals("1A2");
		assert incrementName("1A19").equals("1A20");
		assert incrementName("{1d}").equals("{1e}");


	}

	public static String convert2Html(String s) {
		if(s==null) return "";

		//Convert special chars
		s = s.replaceAll("&", "&amp;");
		s = s.replaceAll("<", "&lt;");
		s = s.replaceAll(">", "&gt;");

		//convert tab to tables;
		StringBuilder sb = new StringBuilder();
		boolean inTable = false;
		String[] lines = s.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			if(line.indexOf('\t')<0) {
				if(inTable) {
					sb.append("</table>");
					inTable = false;
				}
				sb.append(line);
				if(i<lines.length-1) sb.append("<br>");
			} else {
				if(inTable) {
					sb.append("<tr><td>" + line.replaceAll("\t", "</td><td>") + "</td></tr>");

				} else {
					inTable = true;
					sb.append("<table style='border:solid 1px gray'>");
					sb.append("<tr><th>" + line.replaceAll("\t", "</th><th>") + "</th></tr>");

				}
			}
		}
		if(inTable) sb.append("</table>");

		s = sb.toString();
		return s;
	}


	public static String repeat(String s, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static<T> List<T> listOf(T... elts) {
		List<T> res = new ArrayList<>();
		for (T t : elts) {
			res.add(t);
		}
		return res;
	}

	public static<T> List<T> listOf(Collection<T>... elts) {
		List<T> res = new ArrayList<>();
		for (Collection<T> t : elts) {
			res.addAll(t);
		}
		return res;
	}

	public static<T> SortedSet<T> setOf(T... elts) {
		SortedSet<T> res = new TreeSet<>();
		for (T t : elts) {
			res.add(t);
		}
		return res;
	}
	public static<T> SortedSet<T> setOf(Collection<T>... elts) {
		SortedSet<T> res = new TreeSet<>();
		for (Collection<T> t : elts) {
			res.addAll(t);
		}
		return res;
	}

	public static<T, V> Map<T, V> mapOf(T key, V value) {
		Map<T, V> res = new HashMap<>();
		res.put(key, value);
		return res;
	}
	public static<T, V> Map<T, V> mapOf(List<T> keys, List<V> values) {
		Map<T, V> res = new HashMap<>();
		if(keys.size()!=values.size()) throw new IllegalArgumentException("Sizes don't match");
		for (int i = 0; i < keys.size(); i++) {
			res.put(keys.get(i), values.get(i));
		}
		return res;
	}

	public static int getIndexFirstDigit(String s) {
		if(s==null) return -1;
		for (int i = 0; i < s.length(); i++) {
			if(Character.isDigit(s.charAt(i))) return i;
		}
		return -1;
	}


	/**
	 * Returns a summary message with the differences between the 2 collections in max ca.20 characters.
	 * If the 2 sets are equals returns null.
	 *
	 * @param c1
	 * @param c2
	 * @param customComparator
	 * @return
	 */
	public static<T> String diffCollectionsSummary(Collection<T> c1, Collection<T> c2, Comparator<T> customComparator) {
		Set<T>[] diff = diffCollections(c1, c2, customComparator);
		if(diff[0].size()==0 && diff[1].size()==0 && diff[2].size()==0) {
			return null;
		} else if(diff[0].size()>0 && diff[1].size()==0 && diff[2].size()==0) {
			return "added " + (diff[0].size()==1? diff[0].iterator().next(): diff[0].size());
		} else if(diff[0].size()==0 && diff[1].size()>0 && diff[2].size()==0) {
			return "modified " + (diff[1].size()==1? diff[1].iterator().next(): diff[1].size());
		} else if(diff[0].size()==0 && diff[1].size()==0 && diff[2].size()>0) {
			return "removed " + (diff[2].size()==1? diff[2].iterator().next(): diff[2].size());
		} else {
			return "updated";
		}
	}

	/**
	 * Returns an array containing the following elements:
	 * <li>[0] elts in c1 but not in c2
	 * <li>[1] elts in c1 such as elt1.equals(elt2) but customComparator.compare(elt1, elt2)!=0
	 * <li>[2] elts not in c1 but in c2
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static<T> Set<T>[] diffCollections(Collection<T> c1, Collection<T> c2, Comparator<T> customComparator) {
		//The first set contains all elements of c1 not in c2
		Set<T> s0 = new LinkedHashSet<>(c1);
		s0.removeAll(c2);

		//The second set contains all elements of c1 present in c2, but different from the customComparator comparison
		Set<T> s1 = new LinkedHashSet<>();
		if(customComparator!=null) {
			for (T t1 : c1) {
				for (T t2 : c2) {
					if(t1.equals(t2) && customComparator.compare(t1, t2)!=0) {
						s1.add(t1);
						break;
					}
				}
			}
		}

		//The third set contains all elements of c2 not in c1
		Set<T> s2 = new LinkedHashSet<>(c2);
		s2.removeAll(c1);

		return new Set[]{s0, s1, s2};
	}


	/**
	 * Returns the longest common substring
	 * @param a
	 * @param b
	 * @return
	 */
	public static String lcs(String a, String b) {
		int[][] lengths = new int[a.length()+1][b.length()+1];

		// row 0 and column 0 are initialized to 0 already

		for (int i = 0; i < a.length(); i++)
			for (int j = 0; j < b.length(); j++)
				if (a.charAt(i) == b.charAt(j))
					lengths[i+1][j+1] = lengths[i][j] + 1;
				else
					lengths[i+1][j+1] =
					Math.max(lengths[i+1][j], lengths[i][j+1]);

		// read the substring out from the matrix
		StringBuffer sb = new StringBuffer();
		for (int x = a.length(), y = b.length();
				x != 0 && y != 0; ) {
			if (lengths[x][y] == lengths[x-1][y])
				x--;
			else if (lengths[x][y] == lengths[x][y-1])
				y--;
			else {
				assert a.charAt(x-1) == b.charAt(y-1);
				sb.append(a.charAt(x-1));
				x--;
				y--;
			}
		}

		return sb.reverse().toString();
	}

	/**
	 * Increment the sampleId/containerId by appending .1,.2,3,... if needed
	 * @param sampleId
	 * @return
	 */
	public static String incrementId(String sampleId) {
		if(sampleId.indexOf('.')>0) {
			try {
				int suffix = Integer.parseInt(sampleId.substring(sampleId.indexOf('.')+1));
				return sampleId.substring(0, sampleId.indexOf('.')+1) + String.valueOf(suffix+1);
			} catch(Exception e) {
				//Switch to normal mode
			}

		}
		return sampleId+".1";
	}


	/**
	 * Map the objects to their respective class
	 * @param objects
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Map<Class, List<Object>> mapClasses(List<? extends Object> objects) {
		Map<Class, List<Object>> res = new LinkedHashMap<>();
		for (Object object : objects) {
			List<Object> l = res.get(object.getClass());
			if(l==null) res.put(object.getClass(), l = new ArrayList<>());
			l.add(object);
		}
		return res;

	}
	
	public static boolean isNumeric(String string) {
		if(string!=null) {			
			return string.matches("^[-+]?\\d+(\\.\\d+)?$");
		}
		return false;
	}
	
    public static Date parseUTCDateWithOffset(String date) {
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        OffsetDateTime odtInstanceAtOffset = OffsetDateTime.parse(date, DATE_TIME_FORMATTER);

        return Date.from(odtInstanceAtOffset.toInstant());
    }
    
    public static int booleanToInt(boolean boolVal) {
    	int val = (boolVal) ? 1 : 0;
    	return val;
    }
    
    public static boolean intToBoolean(int val) {
    	boolean res = val == 1 ? true : false;
    	return res;
    }
    
    public static byte[] toByteArray(BufferedImage image, String type) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()){
            ImageIO.write(image, type, out);
            return out.toByteArray();
        }
    }
    
    
    public static boolean checkDiffDays(Date monthAgo, SynthesisLibraryOrderDto order) {
		long diff = monthAgo.getTime() - order.getToTime().getTime();
		TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		if(TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) >= 0) {
			return true;
		}
		return false;
	}
    
    public static String increment(int refNumber) {
    	String number = String.valueOf(refNumber);
        Pattern compile = Pattern.compile("^(.*?)([9Z]*)$");
        Matcher matcher = compile.matcher(number);
        String left="";
        String right="";
        if(matcher.matches()){
             left = matcher.group(1);
             right = matcher.group(2);
        }
        number = !left.isEmpty() ? Long.toString(Long.parseLong(left, 36) + 1,36):"";
        number += right.replace("Z", "A").replace("9", "0");
        return number.toUpperCase();
    }
    
    public static String[] charArray(int length) {   
        String[] res = new String[length];
        int counter = 0;
        for(int i = 0; counter < length; i++)
        {
            String name = "";
            int colNumber = i;
            while(colNumber > 0 && colNumber % 27 != 0)
            {  
                char c = (char) ('A' + ((colNumber) % 27) - 1);
                name = c + name;
                colNumber = colNumber / 27;
            }
            res[counter] = name;

            if (i % 27 != 0) {
                counter++;
            }
        }
        return res;
    }
    
    public static LocalDateTime convertDateToLocalDateTime(Date dateToConvert) {
        return dateToConvert.toInstant()
          .atZone(ZoneId.systemDefault())
          .toLocalDateTime();
    }
    
    public static Date convertLocalDateTimeToDate(LocalDateTime localDateTimeToConvert) {
    	return Date.from(localDateTimeToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
