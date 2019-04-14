/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.opennfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.opennfrdroid.helpers.SimpleHttpClient;
import org.opennfr.opennfrdroid.helpers.enigma2.Request;
import org.opennfr.opennfrdroid.parsers.enigma2.saxhandler.E2SimpleListHandler;

import java.util.ArrayList;

/**
 * @author sre
 *
 */
public abstract class AbstractSimpleListRequestHandler {
	private String mUri;
	private E2SimpleListHandler mHandler;
	
	/**
	 * @param uri
	 * @param handler
	 */
	public AbstractSimpleListRequestHandler(String uri, E2SimpleListHandler handler){
		mUri = uri;
		mHandler = handler;
	}
	
	/**
	 * @param shc
	 * @return
	 */
	public String getList(SimpleHttpClient shc){
		return Request.get(shc, mUri);
	}
	
	/**
	 * @param xml
	 * @param list
	 * @return
	 */
	public boolean parseList(String xml, ArrayList<String> list){
		return Request.parseList(xml, list, mHandler);
	}
}
