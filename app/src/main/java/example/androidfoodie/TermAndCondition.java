package example.androidfoodie;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class TermAndCondition extends AppCompatActivity {
TextView text;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_and_condition);

        text=(TextView)findViewById(R.id.textT);
        text.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
    }
}
