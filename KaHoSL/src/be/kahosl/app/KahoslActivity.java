package be.kahosl.app;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.Menu;

public class KahoslActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kahosl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_kahosl, menu);
        return true;
    }
}
