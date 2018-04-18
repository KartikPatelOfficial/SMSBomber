package co.deucate.smsbomber;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    EditText mEmailET,mPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser()!=null){
            parshUser();
        }
        setContentView(R.layout.activity_login);

        mEmailET = findViewById(R.id.emailET);
        mPasswordET = findViewById(R.id.passwordET);

        findViewById(R.id.loginBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailET.getText().toString();
                String password = mPasswordET.getText().toString();
                loginWithEmailAndPassword(email,password);
            }
        });

    }

    private void loginWithEmailAndPassword(final String email, final String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    setDataToDatabase(email,password);
                }else {
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setDataToDatabase(String email, String password) {

    }

    private void parshUser() {
        startActivity(new Intent(this,HomeActivity.class));
    }

    public void forgotPassword(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(""));
        startActivity(intent);
    }

    public void createNewAccount(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(""));
        startActivity(intent);
    }
}
