/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.nfrdroid.helpers.enigma2.URIStore;
import org.opennfr.nfrdroid.parsers.enigma2.saxhandler.E2LocationHandler;

/**
 * @author sre
 *
 */
public class LocationListRequestHandler extends AbstractSimpleListRequestHandler{
	public LocationListRequestHandler(){
		super(URIStore.LOCATIONS, new E2LocationHandler());
	}
}