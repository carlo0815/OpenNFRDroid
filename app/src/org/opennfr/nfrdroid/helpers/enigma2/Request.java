/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.helpers.enigma2;

import org.opennfr.nfrdroid.dataProviders.SaxDataProvider;
import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.NameValuePair;
import org.opennfr.nfrdroid.helpers.SimpleHttpClient;
import org.opennfr.nfrdroid.parsers.GenericSaxParser;
import org.opennfr.nfrdroid.parsers.enigma2.saxhandler.E2ListHandler;
import org.opennfr.nfrdroid.parsers.enigma2.saxhandler.E2SimpleHandler;
import org.opennfr.nfrdroid.parsers.enigma2.saxhandler.E2SimpleListHandler;

import java.util.ArrayList;

/**
 * @author sre
 * 
 */
public class Request {
    public static String get(SimpleHttpClient shc, String uri){
        return get(shc, uri, new ArrayList<>());
    }

	public static String get(SimpleHttpClient shc, String uri, ArrayList<NameValuePair> params) {
		if (shc.fetchPageContent(uri, params)) {
			return shc.getPageContentString();
		}

		return null;
	}

	public static byte[] getBytes(SimpleHttpClient shc, String uri, ArrayList<NameValuePair> params) {
		if (shc.fetchPageContent(uri, params)) {
			return shc.getBytes();
		}
		return new byte[0];
	}
	
	public static byte[] getBytes(SimpleHttpClient shc, String uri) {
		if (shc.fetchPageContent(uri)) {
			return shc.getBytes();
		}

		return new byte[0];
	}

	/**
	 * @param xml
	 * @param result
	 * @param handler
	 * @return
	 */
	public static boolean parse(String xml, ExtendedHashMap result, E2SimpleHandler handler) {
		SaxDataProvider sdp = new SaxDataProvider(new GenericSaxParser());
		handler.setMap(result);
		sdp.getParser().setHandler(handler);
		return sdp.parse(xml);
	}

	/**
	 * @param xml
	 * @param list
	 * @param handler
	 * @return
	 */
	public static boolean parseList(String xml, ArrayList<ExtendedHashMap> list, E2ListHandler handler) {
		SaxDataProvider sdp = new SaxDataProvider(new GenericSaxParser());
		handler.setList(list);
		sdp.setHandler(handler);

		return sdp.parse(xml);
	}

	/**
	 * @param xml
	 * @param list
	 * @param handler
	 * @return
	 */
	public static boolean parseList(String xml, ArrayList<String> list, E2SimpleListHandler handler) {
		SaxDataProvider sdp = new SaxDataProvider(new GenericSaxParser());
		handler.setList(list);
		sdp.setHandler(handler);

		return sdp.parse(xml);
	}
}
