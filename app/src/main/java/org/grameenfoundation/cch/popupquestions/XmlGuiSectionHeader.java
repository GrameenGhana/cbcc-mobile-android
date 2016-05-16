/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package org.grameenfoundation.cch.popupquestions;

import org.digitalcampus.mobile.learningGF.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiSectionHeader extends LinearLayout {
	TextView label;
	
	@SuppressLint("NewApi")
	public XmlGuiSectionHeader(Context context,String labelText,String initialText) {
		super(context);
		label = new TextView(context);
		label.setText(labelText);
		label.setTextColor(getResources().getColor(R.color.WhileWaitingForTransport));
		label.setTextSize(17);
		label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		
		if(!initialText.equals("")&&labelText.contains("Take Action")){
			switch(initialText){
			case "Red":
				label.setTextColor(getResources().getColor(R.color.Red));
				break;
			case "Amber":
				label.setTextColor(getResources().getColor(R.color.Curry));
				break;
			case "Green":
				label.setTextColor(getResources().getColor(R.color.Green));
				break;
			}
		}
		if(labelText.contains("while waiting for transport")){
			label.setTextColor(getResources().getColor(R.color.WhileWaitingForTransport));
			Drawable img = getContext().getResources().getDrawable( R.drawable.ic_transport );
			img.setBounds( 0, 0, 40, 40 );
			label.setCompoundDrawables(null, null, img, null);		
			label.setTextSize(20);
			label.setTextAlignment(TEXT_ALIGNMENT_CENTER);
		}
		this.addView(label);
	}

	public XmlGuiSectionHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	

}
