//package com.jwetherell.quick_response_code;
//
//import java.sql.Connection;
//import java.sql.Date;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.text.BreakIterator;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.GregorianCalendar;
//import java.util.HashMap;
//import java.util.Locale;
//import java.util.Map;
//
//import android.annotation.SuppressLint;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.FragmentManager;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.content.res.Configuration;
//import android.content.res.Resources;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Color;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.StrictMode;
//import android.support.v4.app.FragmentActivity;
//import android.text.InputType;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.PopupMenu;
//import android.widget.PopupMenu.OnMenuItemClickListener;
//import android.widget.SimpleAdapter;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.jwetherell.quick_response_code.*;
//
////import com.example.zxing.*;
//
//public class Content extends Activity implements OnItemClickListener {
//
//	private Locale myLocale;
//	MenuItem manuaItem, scanItem, clearItem;
//	Button btExit, btBoPhan, btCheckout, btngay, btrefresh;
//	EditText editdepartment;
//	ListView l1, l2;
//	int countSelect = 0;
//	String depname;
//	CheckBox cb;
//
//	String nameAction;
//
//	final Context context = this;
//
//	ArrayList<String> arrBophan = new ArrayList<String>();
//	ArrayList<String> arrIdDeps = new ArrayList<String>();
//
//	ArrayList<Model> model = new ArrayList<Model>();
//	ArrayList<String> nameArr = new ArrayList<String>();
//	ArrayList<String> empIDArr = new ArrayList<String>();
//	ArrayList<String> depIDArr = new ArrayList<String>();
//	ArrayList<String> depNMArr = new ArrayList<String>();
//
//	ArrayList<String> depNameArr = new ArrayList<String>();
//
//	Map<String, String> scan = new HashMap<String, String>();
//
//	CustomArrayAdapterListView adapter;
//
//	java.util.Date dt;
//	String ngaythangnam = "";
//	Statement statementl;
//	private ProgressDialog progressDialog;
//	@Override
//	public void onCreate(Bundle s) {
//		super.onCreate(s);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		setContentView(R.layout.content);
//		getWindow().setSoftInputMode(
//			    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//			);
////		btExit = (Button) findViewById(R.id.closeButton);
////		btBoPhan = (Button) findViewById(R.id.btbophan);
////		btCheckout = (Button) findViewById(R.id.btCheckout);
////		editdepartment = (EditText) findViewById(R.id.editextbophan);
////		btngay = (Button) findViewById(R.id.toButton);
////		btrefresh =(Button)findViewById(R.id.btrefresh);
////
////
////		editdepartment.setEnabled(false);
////		btngay.setEnabled(false);
////
////		l1 = (ListView) findViewById(R.id.deplistViewContent);
////		l1.setVisibility(View.GONE);
//		l2 = (ListView) findViewById(R.id.mainListViewContent);
//		l2.setScrollingCacheEnabled(false);
//		// l2.setVisibility(View.VISIBLE);
//
//		adapter = new CustomArrayAdapterListView(this, R.layout.rowlishview, model, context);
//		l2.setAdapter(adapter);
//		l2.setOnItemClickListener(this);
//
//		Singleton.getInstance().setTagCheckOut(-1);
//
//		manuaItem = (MenuItem) findViewById(R.id.one);
//		scanItem = (MenuItem) findViewById(R.id.two);
//		clearItem = (MenuItem) findViewById(R.id.three);
//
//		Calendar c = GregorianCalendar.getInstance();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
//		int year = c.get(Calendar.YEAR);
//		int month = c.get(Calendar.MONTH) + 1;
//		int date = c.get(Calendar.DATE);
//		ngaythangnam = "" + year + "/" + month + "/" + date;
//		btngay.setText(ngaythangnam);
//
//		ReloadUI();
//
//		btBoPhan.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				if (countSelect % 2 == 0) {
//					loadDepsdata();
//					//new doLoadDep().execute();
//					l1.setVisibility(View.VISIBLE);
//					countSelect = 1;
//				} else {
//					if (countSelect % 2 == 0) {
//						l1.setVisibility(View.VISIBLE);
//					}
//					if (countSelect % 2 == 1) {
//						l1.setVisibility(View.GONE);
//						// l1.setVisibility(View.VISIBLE);
//					}
//					countSelect++;
//					if (countSelect == 2) {
//						countSelect = 0;
//					}
//				}
//				// Intent i = new Intent(Content.this, Personnel.class);
//				// startActivity(i);
//			}
//		});
//		btrefresh.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				refresh();
//			}
//		});
//
//		btExit.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//						context);
//				alertDialogBuilder.setTitle(R.string.Notify);
//				// set dialog message
//				alertDialogBuilder
//						.setMessage(R.string.You_will_want_exit_program)
//						.setCancelable(false)
//						.setPositiveButton(R.string.Yes,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int id) {
//										// if this button is clicked, close
//										// current activity
//										Content.this.finish();
//									}
//								})
//						.setNegativeButton(R.string.No,
//								new DialogInterface.OnClickListener() {
//									public void onClick(DialogInterface dialog,
//											int id) {
//										// if this button is clicked, just
//										// close
//										// the dialog box and do nothing
//										dialog.cancel();
//									}
//								});
//
//				// create alert dialog
//				AlertDialog alertDialog = alertDialogBuilder.create();
//
//				// show it
//				alertDialog.show();
//				// set title
//			}
//		});
//
//		btCheckout.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				if (depname == null) {
//					// int language = Singleton.getInstance().getLanguage();
//					// if (language == 0) {
//
//					AlertDialog.Builder alertDialog = new AlertDialog.Builder(
//							context);
//					// Setting Dialog Title
//					alertDialog.setTitle(R.string.Select_a_department)
//					.setCancelable(false)
//					// Setting OK Button
//					.setPositiveButton(R.string.Ok,
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//								}
//							});
//
//					alertDialog.show();
//				} else {
//					PopupMenu popup = new PopupMenu(Content.this, btCheckout);
//					String language = Singleton.getInstance().getLanguage1();
//
//					switch (language) {
//					case "en": {
//						popup.getMenuInflater().inflate(R.menu.popup_menu,
//								popup.getMenu());
//					}
//						break;
//					case "vi": {
//						popup.getMenuInflater().inflate(R.menu.popup_menu_vn,
//								popup.getMenu());
//					}
//						break;
//					case "zh": {
//						popup.getMenuInflater().inflate(R.menu.popup_menu_cn,
//								popup.getMenu());
//					}
//					}
//
//					popup.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//
//						@Override
//						public boolean onMenuItemClick(MenuItem item) {
//							// TODO Auto-generated method stub
//							nameAction = (String) item.getTitle();
//
//							switch (item.getItemId()) {
//							case R.id.one:
//								// btSave.setEnabled(true);
//								Singleton.getInstance().setTagCheckOut(0);
//								Singleton.getInstance().setIdscan("Manual");
//								Toast toast = Toast.makeText(Content.this,
//										nameAction, 1);
//								toast.show();
//								break;
//							case R.id.two:
//								Singleton.getInstance().setTagCheckOut(1);
//								Singleton.getInstance().setIdscan("Scan");
//								//Content.this.finish();
//								Intent simple = new Intent(Content.this,
//										CaptureActivity.class);
//								startActivity(simple);
//
//								break;
//							case R.id.three:
//
//								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//										context);
//								alertDialogBuilder.setTitle(R.string.Warning);
//
//								alertDialogBuilder
//										.setMessage(
//												R.string.Do_you_want_delete_all_of_timekeeper_datas_today)
//										.setCancelable(false)
//										.setPositiveButton(
//												R.string.Yes,
//												new DialogInterface.OnClickListener() {
//													public void onClick(
//															DialogInterface dialog,
//															int id) {
//														// if this button is
//														// clicked, close
//														// current activity
//														for (int i = 0; i < model
//																.size(); i++) {
//															model.get(i)
//																	.setSelected(
//																			false);
//														}
//														Singleton
//																.getInstance()
//																.setTagCheckOut(
//																		1);
//														String sql = "Delete from FILC06D where INT_DT = (CONVERT(nvarchar, GETDATE(),103))";
//														ResultSet rsdelete = null;
//														try {
//															rsdelete = statementl
//																	.executeQuery(sql);
//
//														} catch (SQLException e) {
//															// TODO
//															// Auto-generated
//															// catch block
//															e.printStackTrace();
//														}
//
//														Toast.makeText(
//																Content.this,
//																R.string.Delete_success,
//																1).show();
//
//														l2.invalidateViews();
//													}
//												})
//										.setNegativeButton(
//												R.string.No,
//												new DialogInterface.OnClickListener() {
//													public void onClick(
//															DialogInterface dialog,
//															int id) {
//														dialog.cancel();
//													}
//												});
//								AlertDialog alertDialog = alertDialogBuilder
//										.create();
//
//								// show it
//								alertDialog.show();
//
//								break;
//
//							default:
//								break;
//
//							}
//							l2.invalidateViews();
//							// notifyDataSetChanged();
//							return true;
//						}
//					});
//					popup.show();
//				}
//			}
//		});
//
//	}
//
//	public void ReloadUI() {
//
//		// btSave.setText("Save");
//		btExit.setText(R.string.Exit);
//		btBoPhan.setText(R.string.Department);
//		btCheckout.setText(R.string.TimeKeeping);
//		btrefresh.setText(R.string.Refresh);
//	}
//
//	public void  refresh(){
//		//String depname = Singleton.getInstance().getDepViewing();
//		if(editdepartment.getText().toString().equals("")){
//			return;
//		}
//		else{
//			editdepartment.setText(depname);
//			l2.setVisibility(View.VISIBLE);
//			countSelect = 0;
//			View f = getViewByPosition(2, l1);
//			f.setBackgroundColor(0xdd8808);
//
//			if (depname == null) {
//				AlertDialog alertDialog = new AlertDialog.Builder(
//						Content.this).create();
//				// Setting Dialog Title
//				alertDialog.setTitle(R.string.Select_a_department);
//				// Setting OK Button
//				alertDialog.setButton("Ok",
//						new DialogInterface.OnClickListener() {
//
//							@Override
//							public void onClick(DialogInterface dialog,
//									int which) {
//								// TODO Auto-generated method stub
//							}
//						});
//
//				alertDialog.show();
//			} else {
//
//				//new doLoadData().execute();
//				adapter.notifyDataSetChanged();
//
//			}
//		}
//	}
//
//	public class doLoadDep extends AsyncTask<Void, Void, Void>{
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			// TODO Auto-generated method stub
//			Connect();
//			arrBophan.clear();
//			arrIdDeps.clear();
//			ResultSet rs = null;
//			try {
//				rs = statementl
//						.executeQuery("select DEP_ID, DEP_NM from FILA02A");
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			// Log.e("loi o day", "ket noi chua thanh cong");
//			try {
//				while (rs.next()) {
//					String depid = rs.getString(1);
//					String depname = rs.getString(2);
//					arrIdDeps.add(depid);
//					arrBophan.add(depname);
//				}
//			} catch (SQLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			setAdapterCustomListView(arrBophan, Content.this);
//
//			l1.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//						long arg3) {
//					// TODO Auto-generated method stub
//					depname = arrBophan.get(arg2);
//					editdepartment.setText(depname);
//					Singleton.getInstance().setDepViewing(depname);
//					l1.setVisibility(View.GONE);
//					l2.setVisibility(View.VISIBLE);
//					countSelect = 0;
//					View f = getViewByPosition(2, l1);
//					f.setBackgroundColor(0xdd8808);
//
//					if (depname == null) {
//						AlertDialog alertDialog = new AlertDialog.Builder(
//								Content.this).create();
//						// Setting Dialog Title
//						alertDialog.setTitle(R.string.Select_a_department);
//						// Setting OK Button
//						alertDialog.setButton("Ok",
//								new DialogInterface.OnClickListener() {
//
//									@Override
//									public void onClick(DialogInterface dialog,
//											int which) {
//										// TODO Auto-generated method stub
//									}
//								});
//
//						alertDialog.show();
//					} else {
//
//						//Update();
//						new doLoadData().execute();
//					}
//				}
//			});
//
//			if (arrBophan.size() > 0) {
//				String namedep = arrBophan.get(0);
//				depname = namedep;
//				editdepartment.setText(depname);
//			}
//			super.onPostExecute(result);
//
//
//		}
//
//	}
//
//	public void loadDepsdata() {
//		Connect();
//		arrBophan.clear();
//		arrIdDeps.clear();
//		ResultSet rs = null;
//		try {
//			rs = statementl
//					.executeQuery("select DEP_ID, DEP_NM from FILA02A");
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		// Log.e("loi o day", "ket noi chua thanh cong");
//		try {
//			while (rs.next()) {
//				String depid = rs.getString(1);
//				String depname = rs.getString(2);
//				arrIdDeps.add(depid);
//				arrBophan.add(depname);
//			}
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		setAdapterCustomListView(arrBophan, Content.this);
//
//		l1.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//					long arg3) {
//				// TODO Auto-generated method stub
//				depname = arrBophan.get(arg2);
//				editdepartment.setText(depname);
//				Singleton.getInstance().setDepViewing(depname);
//				l1.setVisibility(View.GONE);
//				l2.setVisibility(View.VISIBLE);
//				countSelect = 0;
//				View f = getViewByPosition(2, l1);
//				f.setBackgroundColor(0xdd8808);
//
//				if (depname == null) {
//					AlertDialog alertDialog = new AlertDialog.Builder(
//							Content.this).create();
//					// Setting Dialog Title
//					alertDialog.setTitle(R.string.Select_a_department);
//					// Setting OK Button
//					alertDialog.setButton("Ok",
//							new DialogInterface.OnClickListener() {
//
//								@Override
//								public void onClick(DialogInterface dialog,
//										int which) {
//									// TODO Auto-generated method stub
//								}
//							});
//
//					alertDialog.show();
//				} else {
//
//					//Update();
//					new doLoadData().execute();
//				}
//			}
//		});
//
//		if (arrBophan.size() > 0) {
//			String namedep = arrBophan.get(0);
//			depname = namedep;
//			editdepartment.setText(depname);
//		}
//
//	}
//
//
//public void Update() {
//		Connect();
//		String jQueryStr = ("select  F1.EMP_ID, F1.EMP_NM, F1.DEP_ID, F2.DEP_NM from  FILB01A F1, FILA02A F2 where (F1.DEP_ID = F2.DEP_ID) and  (F2.DEP_NM = '"
//				+ depname + "')");
//		ResultSet rs = null;
//
//		try {
//			rs = statementl.executeQuery(jQueryStr);
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//
//		model.clear();
//		empIDArr.clear();
//		nameArr.clear();
//		depIDArr.clear();
//		depNMArr.clear();
//		int i=0;
//		try {
//			while (rs.next()) {
//
//				String emID = rs.getString(1);
//				String emName = rs.getString(2);
//
//				String depID = rs.getString(3);
//				String depNM = rs.getString(4);
//				Model ml = new Model();
//				ml.setId(emID);
//				ml.setName(emName);
//				nameArr.add(emName);
//				empIDArr.add(emID);
//				depIDArr.add(depID);
//				depNMArr.add(depNM);
//				model.add(ml);
//				i++;
//				if(i==20)
//				{
//					System.gc();
//				}
//			}
//
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		Singleton.getInstance().setEmpIDArr(empIDArr);
//		Singleton.getInstance().setnameArr(nameArr);
//
////		setAdapterForListView(model, Content.this);
//		//Toast.makeText(getApplicationContext(), ""+model.size(), Toast.LENGTH_LONG).show();
//	}
//
//
////	public void setAdapterForListView(ArrayList<Model> model, Context context) {
////		CustomArrayAdapterListView adapter = new CustomArrayAdapterListView(
////				this, R.layout.rowlishview, model, context);
////		// adapter.notifyDataSetChanged();
////		l2.setAdapter(adapter);
////		l2.setOnItemClickListener(this);
////	}
//
//	public void setAdapterCustomListView(ArrayList<String> array,
//			Context context) {
//		CustomListview adapter = new CustomListview(this, R.layout.row_dep,
//				array);
//		l1.setAdapter(adapter);
//		l1.setOnItemClickListener(this);
//	}
//
//	// Get Item listview
//	public View getViewByPosition(int pos, ListView listView) {
//		final int firstListItemPosition = listView.getFirstVisiblePosition();
//		final int lastListItemPosition = firstListItemPosition
//				+ listView.getChildCount() - 1;
//
//		if (pos < firstListItemPosition || pos > lastListItemPosition) {
//			return listView.getAdapter().getView(pos, null, listView);
//		} else {
//			final int childIndex = pos - firstListItemPosition;
//			return listView.getChildAt(childIndex);
//		}
//	}
//
//	public class doLoadData extends AsyncTask<Void, Void, Void>{
//
//		@Override
//	    protected void onPreExecute() {
//	        super.onPreExecute();
//	        progressDialog = ProgressDialog.show(Content.this, "", "");
//	    }
//
//		@Override
//		protected Void doInBackground(Void... params) {
//			// TODO Auto-generated method stub
//			Connect();
//			String jQueryStr = ("select  F1.EMP_ID, F1.EMP_NM, F1.DEP_ID, F2.DEP_NM, F3.PIC_DR from  FILB01A F1 left join FILA02A F2 on F1.DEP_ID = F2.DEP_ID left join FILB01AB F3 on F1.EMP_ID =F3.EMP_ID where (F2.DEP_NM = '"+depname+"')");
//			ResultSet rs = null;
//			//ResultSet rs1=null;
//			byte image[]=null;
//			//String sql = ("Select PIC_DR from  FILB01AB Where EMP_ID = "+"'"+EMP_ID+"'");
//
//			try {
//				rs = statementl.executeQuery(jQueryStr);
//			} catch (Exception e) {
//				// TODO: handle exception
//				e.printStackTrace();
//			}
//
//			model.clear();
//			empIDArr.clear();
//			nameArr.clear();
//			depIDArr.clear();
//			depNMArr.clear();
//			int i=0;
//			try {
//				while (rs.next()) {
//					Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.noimage);
//					String emID = rs.getString(1);
//					String emName = rs.getString(2);
//
//					String depID = rs.getString(3);
//					String depNM = rs.getString(4);
//					Model ml = new Model();
//					ml.setId(emID);
//					ml.setName(emName);
//
//					image = rs.getBytes(5);
//					if(image!=null){
//						ml.setBipMap(BitmapFactory.decodeByteArray(image, 0, image.length));
//					}
//					if(image==null){
//						ml.setBipMap(bitmap1);
//					}
//					nameArr.add(emName);
//					empIDArr.add(emID);
//					depIDArr.add(depID);
//					depNMArr.add(depNM);
//					model.add(ml);
//					i++;
//					if(i==20)
//					{
//						System.gc();
//					}
//				}
//
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			// TODO Auto-generated method stub
//			progressDialog.dismiss();
//			Singleton.getInstance().setEmpIDArr(empIDArr);
//			Singleton.getInstance().setnameArr(nameArr);
////			setAdapterForListView(model, Content.this);
//			adapter.notifyDataSetChanged();
//			super.onPostExecute(result);
//		}
//
//	}
//
//	public void Connect() {
//
//		String server = Singleton.getInstance().getServer();
//		String database = Singleton.getInstance().getDatabase();
//		String user = Singleton.getInstance().getUser();
//		String pass = Singleton.getInstance().getPassWord();
//
//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
//				.permitAll().build();
//		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//				.detectDiskReads().detectDiskWrites().detectNetwork()
//				.penaltyLog().build());
//		Connection conn = null;
//		String connUrl = null;
//		try {
//			Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
//			connUrl = "jdbc:jtds:sqlserver://" + server + ";" + "databaseName="
//					+ database + ";user=" + user + ";password=" + pass + ";";
//			conn = DriverManager.getConnection(connUrl);
//			statementl = getStatement(conn);
//		} catch (SQLException se) {
//			Log.e("SE", "SE");
//			Log.e("ERROR1", se.getMessage());
//			Log.e("ERROR1", se.getMessage());
//
//			AlertDialog alertDialog = new AlertDialog.Builder(Content.this)
//					.create();
//			// Setting Dialog Title
//			alertDialog.setTitle(se.getMessage());
//			// Setting OK Button
//			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//				}
//			});
//
//			alertDialog.show();
//			// showMessage("Error", se.getMessage());
//		} catch (ClassNotFoundException cl) {
//			Log.e("ER2", "ER2");
//			Log.e("ERROR2", cl.getMessage());
//			Log.e("ERROR2", cl.getMessage());
//			AlertDialog alertDialog = new AlertDialog.Builder(Content.this)
//					.create();
//			// Setting Dialog Title
//			alertDialog.setTitle(cl.getMessage());
//			// Setting OK Button
//			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//				}
//			});
//
//			alertDialog.show();
//			// showMessage("Error", cl.getMessage());
//		} catch (Exception e) {
//			Log.e("ER3", "Couldn't get database connection");
//			Log.e("ERROR3", e.getMessage());
//			Log.e("ERROR3", e.getMessage());
//			AlertDialog alertDialog = new AlertDialog.Builder(Content.this)
//					.create();
//			// Setting Dialog Title
//			alertDialog.setTitle(e.getMessage());
//			// Setting OK Button
//			alertDialog.setButton("Ok", new DialogInterface.OnClickListener() {
//
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//					// TODO Auto-generated method stub
//				}
//			});
//
//			alertDialog.show();
//			// showMessage("Error", e.getMessage());
//		}
//	}
//
//	private Statement getStatement(Connection connection) {
//
//		try {
//			return connection.createStatement();
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//		// TODO Auto-generated method stub
//
//	}
//
//}
