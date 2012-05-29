package es.upm.dit;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class NoteView extends Activity {
    private TextView mTitleText;
    private TextView mBodyText;
    private TextView mEmailText;
	private String mDate;
	private Long mRowId;
	private TextView mDateDisplay;
	private NotesDbAdapter mDbHelper;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);

        mDbHelper.open();
        
        setContentView(R.layout.reminder_view);
        setTitle(R.string.view_note);
        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                                    : null;
        }
        
        mTitleText = (TextView) findViewById(R.id.title);
        mBodyText = (TextView) findViewById(R.id.body);
        mEmailText = (TextView) findViewById(R.id.email);
        
        mDateDisplay = (TextView) findViewById(R.id.dateDisplay);
        
        populateFields();
      
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
	private String convertDate(String query) {
	    	
	    	String[] tokens = query.split("-");
	    	return new String(tokens[2] + "-" + tokens[1] + "-" + tokens[0]);

	    }

}
