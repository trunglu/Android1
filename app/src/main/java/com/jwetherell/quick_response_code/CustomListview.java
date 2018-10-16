//package com.jwetherell.quick_response_code;
//
//import java.util.ArrayList;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.TextView;
//
//public class CustomListview extends ArrayAdapter<String> implements OnItemClickListener{
//
//	private final Activity context;
//	ArrayList<String> arrayDep;
//
//	public CustomListview(Activity context, int layoutID, ArrayList<String> array ) {
//		// TODO Auto-generated constructor stub
//		super(context, R.layout.row_dep, array);
//		this.context=context;
//		this.arrayDep=array;
//	}
//
//	@Override
//	public View getView(final int position, View converView, ViewGroup parent) {
//		TextView txtDepname;
//		LayoutInflater inflator = context.getLayoutInflater();
//		converView = inflator.inflate(R.layout.row_dep, parent, false);
//		txtDepname=(TextView)converView.findViewById(R.id.depTxt);
//
//		if(position%2==0)
//			converView.setBackgroundColor(Color.parseColor("#DCE6F1"));
//		else
//			converView.setBackgroundColor(Color.parseColor("#8DB4E2"));
//
//		txtDepname.setText(arrayDep.get(position).toString());
//		return converView;
//
//	}
//
//	@Override
//   	public void onItemClick(AdapterView<?> parent, View view, int position,
//			long id) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
