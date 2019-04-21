/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.helpers.enigma2.requestinterfaces;

import org.opennfr.nfrdroid.helpers.ExtendedHashMap;
import org.opennfr.nfrdroid.helpers.NameValuePair;
import org.opennfr.nfrdroid.helpers.SimpleHttpClient;

import java.util.ArrayList;

/**
 * @author sre
 *
 */
public interface SimpleRequestInterface {
	String get(SimpleHttpClient shc);
	String get(SimpleHttpClient shc, ArrayList<NameValuePair> params);
	boolean parse(String xml, ExtendedHashMap result);
	ExtendedHashMap getDefault();
}
