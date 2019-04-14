/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.opennfrdroid.helpers.enigma2.requesthandler;

import org.opennfr.opennfrdroid.helpers.enigma2.URIStore;

/**
 * @author sre
 * 
 */
public class TimerChangeRequestHandler extends SimpleResultRequestHandler {
	public TimerChangeRequestHandler() {
		super(URIStore.TIMER_CHANGE);
	}
}
