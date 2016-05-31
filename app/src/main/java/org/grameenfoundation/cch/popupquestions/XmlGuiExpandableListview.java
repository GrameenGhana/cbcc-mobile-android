package org.grameenfoundation.cch.popupquestions;

import org.digitalcampus.mobile.learningGF.R;
import org.grameenfoundation.poc.POCDynamicActivity;

import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XmlGuiExpandableListview extends LinearLayout {
	ExpandableListView label;
	private ListAdapter adapter;
	private String[] opts;
	
	public XmlGuiExpandableListview(final Context context,String[] labelText,String options,final String link) {
		super(context);
		label = new ExpandableListView(context);
		//label.setTextColor(getResources().getColor(R.color.White));
		label.setBackgroundColor(getResources().getColor(R.color.BorderBrown));
		//label.setChildDivider(getResources().getColor(R.color.White));
		//label.setEnabled(true);
		 LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
    		     LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		 this.setLayoutParams(layoutParams);
		 if(options.contains("|")){
			 opts = options.split("\\|");
		 }else{
			 opts=new String[]{"Click here to take action"};
		 }
		
		//System.out.println(opts[1]);
		
	    //label.setDivider(new ColorDrawable(getResources().getColor(R.color.White)));
	    //label.setDividerHeight(1);
		adapter=new ListAdapter(context, labelText,opts,label);
		label.setAdapter(adapter);
		label.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
		label.setOnChildClickListener(new OnChildClickListener() {


			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent=new Intent(context,POCDynamicActivity.class);
				intent.putExtra("link", link);
				context.startActivity(intent);
				return false;
			}
		});
		this.addView(label);
		
	}

	public XmlGuiExpandableListview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	



 class ListAdapter extends BaseExpandableListAdapter{
	 public String[] groupItems;
	 public String[] childItems;
	 public LayoutInflater minflater;
	 private int count;
	 public int lastExpandedGroupPosition;    
	 private Context mContext;
	 ExpandableListView eventsList;

	 public ListAdapter(Context mContext,String[] grList,String[] childList,ExpandableListView eventsList) {
	  groupItems = grList;
	  childItems = childList;
	  this.mContext=mContext;
	  minflater = LayoutInflater.from(mContext);
	  this.eventsList=eventsList;
	 }
	 @Override
	 public long getChildId(int groupPosition, int childPosition) {
		 long id = 0;
			
			
		return id;
	 }

	 @Override
	 public View getChildView(int groupPosition, final int childPosition,
	   boolean isLastChild, View convertView, ViewGroup parent) {
	  
	   if(convertView==null){
		   convertView=minflater.inflate(R.layout.listview_text_single,null);
	   }
	   convertView.setBackgroundColor(getResources().getColor(R.color.White));
		  TextView text1=(TextView) convertView.findViewById(R.id.textView_listViewText);
	   	//	TextView text1=new TextView(mContext);
		   CharSequence t1 = childItems[childPosition];
		   if(((String)t1).contains(("Click here"))){
			   text1.setTextColor(getResources().getColor(R.color.TextColorWine));
		   }
		   SpannableString s1 = new SpannableString(t1);
		   s1.setSpan(new BulletSpan(15), 0, t1.length(), 0);
		   text1.setPadding(40, 0, 0, 0);
		   text1.setText(s1);
	  return convertView;
	 }


	 													
	 @Override
	 public Object getGroup(int groupPosition) {
	  return null;
	 }


	 @Override
	 public View getGroupView(int groupPosition, boolean isExpanded,
	   View convertView, ViewGroup parent) {
		 if( convertView == null ){
		      
			  convertView = minflater.inflate(R.layout.listview_text_single,parent, false);
		    }
		 convertView.setBackgroundDrawable(getResources().getDrawable(R.drawable.card_bakground_selector));
		 TextView text=(TextView) convertView.findViewById(R.id.textView_listViewText);
		 text.setText(groupItems[groupPosition]);
		    return convertView;
	 }

	 
	 																																				
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupItems.length;
	}


	@Override
	public int getChildrenCount(int groupPosition) {
		return childItems.length;
	
	}


	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public String[] getChild(int groupPosition, int childPosition) {
		String[] item = null;

		return item;
			
	}
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

			public void onGroupExpanded(int groupPosition) {
				if(groupPosition != lastExpandedGroupPosition){
		    	    eventsList.collapseGroup(lastExpandedGroupPosition);
		    }
    	
        super.onGroupExpanded(groupPosition);
     
        lastExpandedGroupPosition = groupPosition;
        
    }
 }
	
}