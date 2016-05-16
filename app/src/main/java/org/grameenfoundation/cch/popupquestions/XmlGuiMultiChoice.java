/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package org.grameenfoundation.cch.popupquestions;

import java.util.ArrayList;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.cch.model.Validation;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ArrayAdapter;

public class XmlGuiMultiChoice extends LinearLayout {
	String tag = XmlGuiMultiChoice.class.getName();
	TextView label;
	ArrayAdapter<String> aa;
	private CheckBox checkbox;
	private ArrayList<CheckBox> allCb;
	private JSONObject json;
	private StringBuilder result;
	
	public XmlGuiMultiChoice(Context context,String labelText,String options) {
		super(context);
		label = new TextView(context);
		label.setText(labelText);
		this.addView(label);
		String []opts = options.split("\\|");
        this.setOrientation(LinearLayout.VERTICAL);
        allCb = new ArrayList<CheckBox>();
		 for (int i=0;i<opts.length;i++){
			checkbox=new CheckBox(context);
			checkbox.setText(opts[i]);
			checkbox.setId((1*2)+i);
			allCb.add(checkbox);
			 this.addView(checkbox);
			
		 }
	
	}

	public XmlGuiMultiChoice(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}
	public String getValue()
	{
		String value="";
		//return (String) spinner.getSelectedItem().toString();
		 result=new StringBuilder();
		 for (int i=0;i<allCb.size();i++){
	        	if(allCb.get(i).isChecked()){
	        		
	        		result.append(allCb.get(i).getText().toString()+", ");
	        		//return (String)  result.toString();
	        	}
	}
	return result.toString();
	}
}
