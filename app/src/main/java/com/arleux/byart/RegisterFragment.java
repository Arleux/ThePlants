package com.arleux.byart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.auth.User;

import static com.arleux.byart.ThePlantsFragment.hideKeyboard;

public class RegisterFragment extends Fragment{ //класс с регистрацией пользователя
    private EditText mRegisterEmail;
    private TextView mEmailAlreadyExistException;
    private TextView mEmailNotExistException;
    private TextView mEmailFormatException;
    private EditText mRegisterPassword;
    private TextView mPasswordWeakException;
    private Button mRegisterButton;
    private FirebaseAuth mAuth;
    private ProgressBar mRegisterProgressBar;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUser;

    public static Fragment newInstance(){
        return new RegisterFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance(); //инициализация класса для БД
        mAuthStateListener = new FirebaseAuth.AuthStateListener() { //слушает состояние БД
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser(); //представляет сведения о профиле пользователя в базе данных
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        mRegisterEmail = v.findViewById(R.id.register_email);
        mRegisterPassword = v.findViewById(R.id.register_password);
        mRegisterButton = v.findViewById(R.id.registration_button);
        mEmailAlreadyExistException = v.findViewById(R.id.exception_already_exist_email);
        mEmailNotExistException = v.findViewById(R.id.exception_not_exist_email);
        mEmailFormatException = v.findViewById(R.id.exception_wrong_format_email);
        mPasswordWeakException = v.findViewById(R.id.exception_weak_password);
        mRegisterProgressBar = v.findViewById(R.id.register_progress_bar);

        mRegisterProgressBar.setVisibility(View.GONE);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPasswordWeakException.setText("");
                mEmailAlreadyExistException.setText("");
                mEmailNotExistException.setText("");
                mEmailFormatException.setText("");

                mRegisterProgressBar.setVisibility(View.VISIBLE);

                if(mRegisterEmail.getText().toString().equals("") || mRegisterPassword.getText().toString().equals("")) { //поля не должны быть пустым
                    mRegisterProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.enter_exception_null_field, Toast.LENGTH_LONG).show();
                }
                else {
                    registerUser(mRegisterEmail.getText().toString(), mRegisterPassword.getText().toString()); //если эти условия выполнены, то можно попытаться зарегистрировать
                }
            }
        });
        hideKeyboard(getActivity(), container);  //для того чтобы спрятать клавиатуру при касании пальцем по экрану
        return v;
    }
    @Override
    public void onResume(){
        super.onResume();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    private void registerUser(final String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        mRegisterProgressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification(); //отправляю на этот email подтверждение почты
                            AlertDialog dialogOnFinishButton = new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.confirm_email_message)
                                    .setPositiveButton(R.string.reset_button_next, null)
                                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mUser.delete();
                                            dialogInterface.cancel(); //переходит назад
                                            mRegisterEmail.setText("");
                                            mRegisterPassword.setText("");
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();
                            dialogOnFinishButton.show();
                            //сам реализую лисенер, так как для меня важно, что не при любом клике будет переход с авториции, а только если почта подтверждена
                            dialogOnFinishButton.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    mAuth.addAuthStateListener(mAuthStateListener); //при каждом клике проверяю состояние БД
                                    if(mUser != null){
                                        mUser.reload(); //обнови информацию о нём
                                        if(mUser.isEmailVerified()) { //если этот user подтвердил email, то перейдём к авторизации
                                            AlertDialog dialogOnFinishButton = new AlertDialog.Builder(getActivity())
                                                    .setTitle(R.string.register_congratulations)
                                                    .setCancelable(false)
                                                    .setNegativeButton(R.string.enter_login_button_text, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            startActivity(ThePlantsActivity.newIntent(getActivity()));
                                                        }
                                                    })
                                                    .create();
                                            dialogOnFinishButton.show();
                                        }
                                        else
                                            Toast.makeText(getActivity(), R.string.confirm_email, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                shakeAnimation(mPasswordWeakException);
                                mPasswordWeakException.setText(R.string.exception_weak_password);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                shakeAnimation(mEmailFormatException);
                                mEmailFormatException.setText(R.string.exception_wrong_format_email);
                            } catch (FirebaseAuthUserCollisionException e) {
                                shakeAnimation(mEmailAlreadyExistException);
                                mEmailAlreadyExistException.setText(R.string.exception_already_exist_email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
    public static void shakeAnimation(View view){ //трясет элемент, если что-то неправильно ввёл
        TranslateAnimation animation = new TranslateAnimation(-30,  30, 0, 0);
        animation.setRepeatCount(1);
        animation.setDuration(60);
        view.startAnimation(animation);
    }
}
