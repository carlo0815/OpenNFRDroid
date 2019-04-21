/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.NameValuePair;
import org.opennfr.nfrdroid.helpers.SimpleHttpClient;
import org.opennfr.nfrdroid.helpers.enigma2.Request;
import org.opennfr.nfrdroid.helpers.enigma2.requestinterfaces.ListRequestInterface;
import org.opennfr.nfrdroid.parsers.enigma2.saxhandler.E2ListHandler;

import java.util.ArrayList;

/**
 * @author sre
 *
 */
public abstract class AbstractListRequestHandler implements ListRequestInterface {
	protected String mUri;
	protected E2ListHandler mHandler;
	
	public AbstractListRequestHandler(String uri, E2ListHandler handler){
		mUri = uri;
		mHandler = handler;
	}
	
	/**
	 * @param shc
	 * @param params
	 * @return
	 */
	public String getList(SimpleHttpClient shc, ArrayList<NameValuePair> params) {
		return Request.get(shc, mUri, params);
	}

	/* (non-Javadoc)
	 * @see org.opennfr.nfrdroid.helpers.enigma2.requestinterfaces.ListRequestInterface#getList(org.opennfr.nfrdroid.helpers.SimpleHttpClient)
	 */
	public String getList(SimpleHttpClient shc) {
		return getList(shc, new ArrayList<>());
	}
	
	/**
	 * @param xml
	 * @param list
	 * @return
	 */
	public boolean parseList(String xml, ArrayList<ExtendedHashMap> list) {
		return Request.parseList(xml, list, mHandler);
	}
}
