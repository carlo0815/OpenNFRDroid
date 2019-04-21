/* © 2010 Stephan Reichholf <stephan at reichholf dot net>
 * 
 * Licensed under the Create-Commons Attribution-Noncommercial-Share Alike 3.0 Unported
 * http://creativecommons.org/licenses/by-nc-sa/3.0/
 */

package org.opennfr.nfrdroid.dataProviders;

import org.opennfr.nfrdroid.parsers.GenericSaxParser;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author sreichholf
 * 
 */
public class SaxDataProvider extends AbstractDataProvider {

	/**
	 * @param dp
	 */
	public SaxDataProvider(GenericSaxParser dp) {
		super(dp);
	}

	/**
	 * @param dp
	 */
	public void setParser(GenericSaxParser dp) {
		mParser = dp;
	}
	
	public void setHandler(DefaultHandler handler){
		getParser().setHandler(handler);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.opennfr.nfrdroid.dataProviders.AbstractDataProvider#getParser()
	 */
	public GenericSaxParser getParser() {
		return (GenericSaxParser) mParser;
	}

}
