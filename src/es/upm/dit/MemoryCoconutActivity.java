package es.upm.dit;


import java.util.ArrayList;
import java.util.Calendar;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MemoryCoconutActivity extends ListActivity {

	private static final String LOGGER="MemoCoco";
	private static final int ACTIVITY_CREATE=0;
	private static final int ACTIVITY_EDIT=1;

	private static final int INSERT_ID = Menu.FIRST;
	private static final int DELETE_ID = Menu.FIRST + 1;
	private static final int ACERCADE_ID = Menu.FIRST + 2;
	private static final int AYUDA_ID = Menu.FIRST + 3;
	private static final int EDIT_ID = Menu.FIRST + 4;

	private NotesDbAdapter mDbHelper;
	private NotificationManager mNM;
	private Context ctx;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.reminder_list);

		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.coco_title);
		ctx = this.getBaseContext();
		mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		fillData();
		registerForContextMenu(getListView());
		checkForNotifications();
	}

	private void fillData() {
		Cursor notesCursor = mDbHelper.fetchAllNotes();
		// Get all of the rows from the database and create the item list

		startManagingCursor(notesCursor);

		// Create an array to specify the fields we want to display in the list (only TITLE)
		String[] from = new String[]{NotesDbAdapter.KEY_TITLE, NotesDbAdapter.KEY_F};

		// and an array of the fields we want to bind those fields to (in this case just text1)
		int[] to = new int[]{R.id.text1, R.id.text2};

		// Now create a simple cursor adapter and set it to display
		SimpleCursorAdapter notes = 
				new SimpleCursorAdapter(this, R.layout.reminder_row, notesCursor, from, to);
		setListAdapter(notes);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater(); 
		inflater.inflate(R.menu.menu, menu);
//		menu.add(0, INSERT_ID, 0, R.string.menu_insert);
//		menu.add(0, ACERCADE_ID, 0, R.string.menu_acercade);
//		menu.add(0, AYUDA_ID, 0, R.string.menu_ayuda);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		AdapterContextMenuInfo info =
			      (AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case R.id.insert:
			createNote();
			return true;
		case R.id.acercade:
			mostrarAcercade();
			return true;
		case R.id.ayuda:
			mostrarAyuda();
			return true;
		}

		return super.onMenuItemSelected(featureId, item);
	}
	private void mostrarAcercade() {
		Intent i = new Intent(this, AcercaDeActivity.class);//this referencia al Context
		Log.v(LOGGER,"entrando en AcercaDe");
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	private void mostrarAyuda() {
		Intent i = new Intent(this, AyudaActivity.class);//this referencia al Context
		Log.v(LOGGER,"entrando en Ayuda");
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		
		MenuInflater inflater = getMenuInflater(); 
		inflater.inflate(R.menu.context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		AdapterContextMenuInfo info =
				(AdapterContextMenuInfo) item.getMenuInfo();
		switch(item.getItemId()) {
		case R.id.delete:

			deleteNote(info.id);
			return true;
		case R.id.edit:
			editNote(info.id);
			
		}
		return super.onContextItemSelected(item);
	}

	private void createNote() {
		Intent i = new Intent(this, NoteEdit.class);
		Log.v(LOGGER,"entrando en noteEdit");
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent i = new Intent(this, NoteView.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id );//el problema parece estar aki
		startActivity(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		fillData();
	}
	private int isOutdated(String date) {
		String[] tokens = date.split("-");
		Calendar rightNow = Calendar.getInstance();
		//Toast.makeText(getBaseContext(), "Date testing: "+"Year: "+tokens[0]+" Month: "+tokens[1]+"Day:"+tokens[2],Toast.LENGTH_SHORT).show();
		Integer nday=rightNow.get(rightNow.DAY_OF_MONTH);

		Integer nmonth=rightNow.get(rightNow.MONTH)+1;

		//Toast.makeText(getBaseContext(),"dia actual: "+nday.toString()+"mes actual: "+nmonth.toString(),Toast.LENGTH_SHORT).show();
		Log.v(LOGGER,new Integer(nmonth.compareTo(new Integer(tokens[1]))).toString());
		if(nmonth<new Integer(tokens[1])){
			return 1; //no esta outdated
		} else if(nmonth.compareTo(new Integer(tokens[1]))==0 && nday<new Integer(tokens[2])){
			return 1; //no esta outdated
		} else if(nmonth.compareTo(new Integer(tokens[1]))==0 && nday.compareTo(new Integer(tokens[2]))==0){
			return 0; //es hoy
		} else {
			//Toast.makeText(getBaseContext(),"dia actual: "+nday+"dia testeado: "+new Integer(tokens[2])+"mes actual: "+nmonth.toString()+" mes testeado: "+new Integer(tokens[1]),Toast.LENGTH_LONG).show();
			return -1; //esta outdated
		}


	}

	private void checkForNotifications(){
		Cursor notesCursor = mDbHelper.fetchAllNotes();
		notesCursor.moveToFirst();

		ArrayList<String> recordatoriosAtrasados=new ArrayList<String>();
		ArrayList<String> recordatoriosHoy=new ArrayList<String>();

		int i = 0;


		while(notesCursor.isAfterLast() == false) { 
			String date= new String(notesCursor.getString(notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_F)));
			if(isOutdated(date)==-1){
				String rec=new String(notesCursor.getString(notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
				recordatoriosAtrasados.add(rec);
			}else if(isOutdated(date)==0){
				String rec=new String(notesCursor.getString(notesCursor.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
				recordatoriosHoy.add(rec);
			}

			i++;
			notesCursor.moveToNext();


		} 

		showNotification(recordatoriosHoy,0);
		showNotification(recordatoriosAtrasados,-1);

	}

	private void showNotification(ArrayList<String> list,int flag) {
		String titleText="";
		if(list.isEmpty()){
			return;
		}else{

			switch(flag){
			case -1:
				titleText="¡Tienes recocordatorios atrasados!";
				break;
			case 0:
				titleText="¡Tienes recocordatorios hoy!";
				break;
			}

			String contentText="Recocordatorios: ";
			for(String s:list){
				contentText=contentText.concat(s);
				contentText=contentText.concat(", ");

			}
			Notification notification = new Notification(R.drawable.icon, titleText,
					System.currentTimeMillis());


			PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
					new Intent(this, MemoryCoconutActivity.class), 0);



			notification.setLatestEventInfo(this, titleText,contentText, contentIntent);


			mNM.notify(flag, notification);
		}
	}

	@Override
	protected void onResume() {
		checkForNotifications();
		super.onResume();
	}
	private void deleteNote(final long id2){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.alert))
		.setCancelable(false)
		.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {


				mDbHelper.deleteNote(id2);
				fillData();


			}
		})
		.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		(builder.create()).show();
	}
	private void editNote(long id2){
		Intent i = new Intent(this, NoteEdit.class);
		i.putExtra(NotesDbAdapter.KEY_ROWID, id2 );
		startActivityForResult(i, ACTIVITY_EDIT);
	}

}