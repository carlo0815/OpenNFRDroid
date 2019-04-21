package org.opennfr.nfrdroid.view.helper;

import android.content.Context;
import android.util.TypedValue;

import androidx.core.content.ContextCompat;

import com.afollestad.materialdialogs.internal.MDTintHelper;

/**
 * Created by reichi on 09/04/15.
 */
public class TintHelper extends MDTintHelper {
	public static int getColorFromAttr(Context context, int attrId){
		TypedValue typedValue = new TypedValue();
		context.getTheme().resolveAttribute(attrId, typedValue, true);

		return ContextCompat.getColor(context, typedValue.resourceId);
	}
}
