package es.upm.dit;


import java.util.Calendar;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MemoryCoconutActivity extends ListActivity {

    private static final String LOGGER="MemoCoco";
	private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;
    private static final int ACERCADE_ID = Menu.FIRST + 2;
    private static final int AYUDA_ID = Menu.FIRST + 3;

    private NotesDbAdapter mDbHelper;
   

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reminder_list);
        mDbHelper = new NotesDbAdapter(this);
        mDbHelper.open();
        fillData();
        registerForContextMenu(getListView());
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
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        menu.add(0, ACERCADE_ID, 0, R.string.menu_acercade);
        menu.add(0, AYUDA_ID, 0, R.string.menu_ayuda);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createNote();
                return true;
            case ACERCADE_ID:
            	mostrarAcercade();
            	return true;
            case AYUDA_ID:
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
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
            	
            	AlertDialog.Builder builder = new AlertDialog.Builder(this);
            	builder.setMessage(getString(R.string.alert))
            	       .setCancelable(false)
            	       .setPositiveButton("S’", new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   
            	        	   AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            	    		   mDbHelper.deleteNote(info.id);
            	    		   fillData();
            	    		   
            	        	   
            	           }
            	       })
            	       .setNegativeButton("No", new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	
            	(builder.create()).show();
            	return true;
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
        
        Intent i = new Intent(this, NoteEdit.class);
        i.putExtra(NotesDbAdapter.KEY_ROWID, id);
       
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
    	 super.onActivityResult(requestCode, resultCode, intent);
    	 
    	    fillData();
    }
    private int isOutdated(String date) {
    	String[] tokens = date.split("-");
    	Calendar rightNow = Calendar.getInstance();
        //Toast.makeText(getBaseContext(), tokens[0]+tokens[1]+tokens[2],Toast.LENGTH_SHORT).show();
        Integer nday=rightNow.get(rightNow.DAY_OF_MONTH);
        
        Integer nmonth=rightNow.get(rightNow.MONTH)+1;
        
       // Toast.makeText(getBaseContext(),"dia: "+nday.toString()+"mes: "+nmonth.toString(),Toast.LENGTH_SHORT).show();
		if(nmonth<new Integer(tokens[1])){
			return 1; //no est‡ outdated
		} else if(nmonth==new Integer(tokens[1]) && nday<new Integer(tokens[0])){
			return 1; //no est‡ outdated
		} else if(nmonth==new Integer(tokens[1]) && nday==new Integer(tokens[0])){
			return 0; //es hoy
		} else {
			return -1; //est‡ outdated
		}
    	
    }
    
    private void checkForNotifications(){
    	Cursor notesCursor = mDbHelper.fetchAllNotes();
    	notesCursor.moveToFirst();
    }
}