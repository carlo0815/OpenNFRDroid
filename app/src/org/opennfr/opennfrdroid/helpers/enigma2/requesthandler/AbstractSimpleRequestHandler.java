/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.opennfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.opennfrdroid.helpers.ExtendedHashMap;
import org.opennfr.opennfrdroid.helpers.NameValuePair;
import org.opennfr.opennfrdroid.helpers.SimpleHttpClient;
import org.opennfr.opennfrdroid.helpers.enigma2.Request;
import org.opennfr.opennfrdroid.helpers.enigma2.requestinterfaces.SimpleRequestInterface;
import org.opennfr.opennfrdroid.parsers.enigma2.saxhandler.E2SimpleHandler;

import java.util.ArrayList;


/**
 * @author sre
 * 
 */
public abstract class AbstractSimpleRequestHandler implements SimpleRequestInterface {
	protected String mUri;
	private E2SimpleHandler mHandler;

	/**
	 * @param uri
	 * @param handler
	 */
	public AbstractSimpleRequestHandler(String uri, E2SimpleHandler handler) {
		mUri = uri;
		mHandler = handler;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.
	 * SimpleRequestParamInterface
	 * #get(org.opennfr.opennfrdroid.helpers.SimpleHttpClient,
	 * java.util.ArrayList)
	 */
	public String get(SimpleHttpClient shc) {
		return get(shc, new ArrayList<>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.
	 * SimpleRequestParamInterface
	 * #get(org.opennfr.opennfrdroid.helpers.SimpleHttpClient,
	 * java.util.ArrayList)
	 */
	public String get(SimpleHttpClient shc, ArrayList<NameValuePair> params) {
		return Request.get(shc, mUri, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.opennfr.opennfrdroid.helpers.enigma2.requesthandler.
	 * SimpleRequestParamInterface#parse(java.lang.String)
	 */
	public boolean parse(String xml, ExtendedHashMap result) {
		if (Request.parse(xml, result, mHandler)) {
			return true;
		} else {
			result.clear();
			result.putAll(getDefault());
			return false;
		}
	}

	public ExtendedHashMap getDefault() {
		return new ExtendedHashMap();
	}
}
