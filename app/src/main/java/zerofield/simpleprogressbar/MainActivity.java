package zerofield.simpleprogressbar;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DotProgressBar mDotProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDotProgressBar = (DotProgressBar) findViewById(R.id.progressBar);

        Button minusButton = (Button) findViewById(R.id.minusButton);
        Button plusButton = (Button) findViewById(R.id.plusButton);

        minusButton.setOnClickListener(this);
        plusButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        int value = mDotProgressBar.getValue();

        if (id == R.id.minusButton) {
            mDotProgressBar.setValue(value - 1);
        } else if (id == R.id.plusButton) {
            mDotProgressBar.setValue(value + 1);
        }
    }
}
