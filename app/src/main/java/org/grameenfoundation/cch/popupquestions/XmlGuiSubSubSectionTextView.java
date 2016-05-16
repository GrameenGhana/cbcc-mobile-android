/*
 * XmlGui application.
 * Written by Frank Ableson for IBM Developerworks
 * June 2010
 * Use the code as you wish -- no warranty of fitness, etc, etc.
 */
package org.grameenfoundation.cch.popupquestions;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.poc.TreatingDiarrhoeaActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiSubSubSectionTextView extends LinearLayout {
	TextView label;
	
	public XmlGuiSubSubSectionTextView(final Context context,String labelText,final String link,String property) {
		super(context);
		  LinearLayout ll = new LinearLayout(context);
          ll.setOrientation(LinearLayout.VERTICAL);
          LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
        		     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
          
          layoutParams.setMargins(90, 0, 0, 0);
		
			 label = new TextView(context);
			 label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
			 switch (property) {
				case "Bold":
					label.setTypeface(null,Typeface.BOLD);
					break;
				case "Bold Red":
					label.setTextColor(getResources().getColor(R.color.Red));
					label.setTypeface(null,Typeface.BOLD);
					break;
				case "Bold Blue":
					label.setTextColor(getResources().getColor(R.color.Blue));
					label.setTypeface(null,Typeface.BOLD);
					break;
				case "Italic":
					label.setTypeface(null,Typeface.ITALIC);
					break;
				case "Italic Red":
					label.setTextColor(getResources().getColor(R.color.Red));
					label.setTypeface(null,Typeface.ITALIC);
					break;
				case "Italic Blue":
					label.setTextColor(getResources().getColor(R.color.Blue));
					label.setTypeface(null,Typeface.ITALIC);
					break;
				default:
					break;
				}
		     if(!link.equals("")){
		    	 Drawable img = getContext().getResources().getDrawable( R.drawable.ic_click );
					img.setBounds( 0, 0, 30, 40 );
					label.setText(labelText);
					label.setCompoundDrawables(img, null, null, null);		
					label.setTextColor(getResources().getColor(R.color.WhileWaitingForTransport));
					label.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if(!link.contains("ors_calculator")){
								Intent intent=new Intent(context,POCDynamicActivity.class);
								intent.putExtra("link", link);
								context.startActivity(intent);
							}else{
								Intent intent=new Intent(context,TreatingDiarrhoeaActivity.class);
								intent.putExtra("value", "cwc");
								context.startActivity(intent);
							}
							
							
						}
					});
		     }else{
		    	 SpannableString content = new SpannableString(labelText);
			     content.setSpan(new BulletSpan(15), 0, content.length(), 0);
			     label.setText(content);
		     }
		    
		     ll.addView(label,layoutParams);
	      
		 this.addView(ll);
		
	}

	public XmlGuiSubSubSectionTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	

}
