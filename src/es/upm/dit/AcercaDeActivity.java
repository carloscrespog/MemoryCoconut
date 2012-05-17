package es.upm.dit;

import android.app.Activity;
import android.os.Bundle;



public class AcercaDeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acercade_view); //Cargamos la vista
		setTitle(R.string.title_acercade);
	}

}
