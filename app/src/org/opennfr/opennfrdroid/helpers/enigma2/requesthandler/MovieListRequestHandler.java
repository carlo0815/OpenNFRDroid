/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.opennfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.opennfrdroid.helpers.enigma2.URIStore;
import org.opennfr.opennfrdroid.parsers.enigma2.saxhandler.E2MovieListHandler;

/**
 * @author sre
 * 
 */
public class MovieListRequestHandler extends AbstractListRequestHandler {
	public MovieListRequestHandler(){
		super(URIStore.MOVIES, new E2MovieListHandler());
	}
}
