package es.upm.dit;

import android.app.Activity;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NoteView extends Activity implements View.OnClickListener{
    private TextView mTitleText;
    private TextView mBodyText;
    private TextView mEmailText;
	private String mDate;
	private Long mRowId;
	private TextView mDateDisplay;
	private NotesDbAdapter mDbHelper;
	private Button mailBtn;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);

        mDbHelper.open();
        
        setContentView(R.layout.reminder_view);
        setTitle(R.string.view_note);
        
        mailBtn=(Button) findViewById(R.id.emailbtn);
        mailBtn.setOnClickListener(this); //this referencia a esta clase, necesita implementar la interfaz View.OnClickListener para k funcione (le obliga a tner metodo onClick)
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
	  @Override
      public void onClick(View v) {
      	final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
      	emailIntent.setType("plain/text");
      	//cargamos la direccion guardad como destinatario
      	emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ 
      			mEmailText.getText().toString()}); 
      	//ponemos como asunto por defecto el titulo del recocordatorio
      	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mTitleText.getText().toString());
      	//ponemos como contenido por defecto la descripcion del recocordatorio
      	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, mBodyText.getText().toString());
      	startActivity(Intent.createChooser(emailIntent, "Send mail..."));

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
