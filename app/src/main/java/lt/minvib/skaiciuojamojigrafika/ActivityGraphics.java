package lt.minvib.skaiciuojamojigrafika;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityGraphics extends AppCompatActivity {

    ImageButton refresh;
    ImageButton undo;

    GrapicView view;




    TextView count;

    CheckBox automatic;

    Button calculate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphics);

        refresh = (ImageButton) findViewById(R.id.refresh);
        undo = (ImageButton) findViewById(R.id.undo);
        view = (GrapicView)findViewById(R.id.hullView);
       // view.setMode(GrapicView.Mode.MODE_BY_STEP);

        count = (TextView) findViewById(R.id.count);
        automatic= (CheckBox) findViewById(R.id.automatic);
        calculate= (Button) findViewById(R.id.calculate);

        view.setPointEnteredListener(new GrapicView.OnPointEnteredListener() {
            @Override
            public void onPointEntered() {
                int [] o = view.getCount();
                count.setText("count: " + o[0] +" / "+ o[1]);
            }
        });

        automatic.setChecked(false);
        view.setAutomaticallyRecalculate(automatic.isChecked());
        automatic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                view.setAutomaticallyRecalculate(isChecked);
                calculate.setVisibility(!isChecked ? View.VISIBLE: View.INVISIBLE);
            }
        });

        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.recalculate();
            }
        });


        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.reset();
            }
        });
        undo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.undo();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
