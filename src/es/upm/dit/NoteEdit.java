

package es.upm.dit;



import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NoteEdit extends Activity {

	
	private static final String LOGGER="MemoCoco";
    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mEmailText;
    private String mDate;
    
    private Long mRowId;

    private TextView mDateDisplay;
    private Button mPickDate;
    static final int DATE_DIALOG_ID = 0;
    
    private int mYear;
    private int mMonth;
    private int mDay;
    
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    
    private NotesDbAdapter mDbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);

        mDbHelper.open();
        
        setContentView(R.layout.reminder_edit);
        setTitle(R.string.edit_note);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mEmailText = (EditText) findViewById(R.id.email);
        
        Button confirmButton = (Button) findViewById(R.id.confirm);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        /*
         * if (savedInstanceState==null{ 
         * 		mRowId= null
         * } else {
         * 		mRowId =(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
         * }
         */
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                                    : null;
        }
        
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        mPickDate = (Button) findViewById(R.id.pickDate);
        
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(DATE_DIALOG_ID);
            }
        });
        
        Calendar c = (Calendar) (Calendar.getInstance()).clone();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        
        populateFields();

        confirmButton.setOnClickListener(new View.OnClickListener() {

        	public void onClick(View view) {
        		
        		if((mTitleText.getText().toString()).equals("") || (mTitleText.getText().toString()).equals(" ")  ||
            			(mBodyText.getText().toString()).equals("") || (mBodyText.getText().toString()).equals(" ") ||
            			(mEmailText.getText().toString()).equals("") || (mEmailText.getText().toString()).equals(" ") ){
        			showErrorMessage("Error", "No se han completado todos los campos");
        		} else{
        			setResult(RESULT_OK);
                    Toast.makeText(getBaseContext(), "Recordatorio guardado",Toast.LENGTH_SHORT).show();
                    finish();
        		}

            			
                
            }
        });
        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {

                    public void onDateSet(DatePicker view, int year, 
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        updateDisplay(mDateDisplay);
                    }
                };
         Log.v(LOGGER,"fin de oncreate alcanzado");
                
    }
    
    private void updateDisplay(TextView mDateDisplay) {
    	
    	String mDay_S = "" + mDay;
    	String mMonth_S = "" + (mMonth+1);
    	
    	if((int) (mMonth+1)/10 < 1) mMonth_S = "0"+(mMonth+1);
    	
    	if((int) mDay/10 < 1) mDay_S = "0"+mDay;
    	
    	mDateDisplay.setText(
            new StringBuilder()
                    .append(mDay_S).append("-")
                    .append(mMonth_S).append("-")
                    .append(mYear).append(" "));
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
        }
        return null;
    }
    
    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                        note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
            mEmailText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_EMAIL)));
            mDate=convertDate(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_F)));
            mDateDisplay.setText(mDate);
            
            
            
        }
    }
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		saveState();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		populateFields();
	}
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}
	 private void saveState() {
		
	        String title = mTitleText.getText().toString();
	        String body = mBodyText.getText().toString();
	        String email =mEmailText.getText().toString();
	        String f = (mDateDisplay.getText().toString()).replaceAll(" ", "");
       
		 	
	        if (mRowId == null) {
	            long id = mDbHelper.createNote(title, body,email,f);
	            if (id > 0) {
	                mRowId = id;
	            }
	        } else {
	            mDbHelper.updateNote(mRowId, title, body,email,f);
	        }
	        
	    }
	 
	 private String convertDate(String query) {
	    	
	    	String[] tokens = query.split("-");
	    	return new String(tokens[2] + "-" + tokens[1] + "-" + tokens[0]);

	    }
	 private void showErrorMessage(String title, String content) {
	    	
	    	AlertDialog alertDialog;
			alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle(title);
			alertDialog.setMessage(content);
			alertDialog.show();
	    	

	    }
}
