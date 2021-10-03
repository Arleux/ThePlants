package com.arleux.byart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class ThePlantsFragment extends Fragment {
    private boolean amIhaveAccount = false; //если нет аккаунта, то когда буду выходить из приложения будет надпись не "Вы хотите выйти из аккаунта", а просто типа хочу выйти из приложения
    private EditText mEnterEmail;
    private TextView mNotRegisterEmail;
    private EditText mEnterPassword;
    private TextView mWrongPassword;
    private TextView mForgetPassword;
    private Button mSignInButton;
    private Button mRegisterIn;
    private Button mSignInButtonWithout;
    private EditText mEnterResetPassword;
    public static FirebaseAuth mAuth;
    private ProgressBar mProgressBarAuth;
    private FirebaseUser mUser;
    public static FirebaseAuth.AuthStateListener mAuthStateListener; //отслеживает входы и выходы пользователя
    private AlertDialog dialogResetPassword;
    private String DEFAULT_USER_ID = "default";

    public static Fragment newInstance(){
        return new ThePlantsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance(); //нужна инициализация
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser(); //представляет сведения о профиле пользователя в базе данных
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_the_plants, container, false);
        mEnterEmail = v.findViewById(R.id.enter_email);
        mEnterPassword = v.findViewById(R.id.enter_password);
        mSignInButton = v.findViewById(R.id.sign_in);
        mSignInButtonWithout = v.findViewById(R.id.sign_in_without);
        mRegisterIn = v.findViewById(R.id.register_in);
        mNotRegisterEmail = v.findViewById(R.id.exception_not_register_email);
        mWrongPassword =v.findViewById(R.id.exception_wrong_password);
        mForgetPassword = v.findViewById(R.id.forget_password);
        mProgressBarAuth = v.findViewById(R.id.progress_bar_auth);

        mProgressBarAuth.setVisibility(View.GONE);
        mRegisterIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = RegisterActivity.newIntent(getActivity()); //перехожу в активность для регистрации
                startActivity(intent);
            }
        });
        mSignInButton.setOnClickListener(new View.OnClickListener(){ //хочу войти по логину и паролю
            @Override
            public void onClick(View view) {
                mProgressBarAuth.setVisibility(View.VISIBLE);
                mNotRegisterEmail.setText("");
                mWrongPassword.setText("");
                if(mEnterEmail.getText().toString().equals("") || mEnterPassword.getText().toString().equals("")) {
                    mProgressBarAuth.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.enter_exception_null_field, Toast.LENGTH_LONG).show();
                }
                else{
                    signIn(mEnterEmail.getText().toString(), mEnterPassword.getText().toString());
                    }
            }
        });
        mForgetPassword.setOnClickListener(new View.OnClickListener() { //по клику предложит ввести email для восстановления пароля
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_forget_password, null);
                mEnterResetPassword = v.findViewById(R.id.enter_email_reset_password);

                dialogResetPassword = new AlertDialog.Builder(getActivity())
                        .setCancelable(false) //чтобы не исчезан нажатием на экран
                        .setView(v)
                        .setTitle(R.string.reset_password_email)
                        .setPositiveButton(R.string.reset_button_next, null)
                        .setNeutralButton(R.string.reset_button_back, null)
                        .show();
                //задаю лисенер таким образом(отдельно), чтобы не пропадал диалог при нажатии на кнопку, если email неправильный
                dialogResetPassword.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isEmailValid(mEnterResetPassword.getText().toString()))
                            resetPassword(mEnterResetPassword.getText().toString());
                        else
                            Toast.makeText(getActivity(), R.string.exception_wrong_format_email, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        mSignInButtonWithout.setOnClickListener(new View.OnClickListener() { //войти без аккаунта
            @Override
            public void onClick(View view) {
                amIhaveAccount =false;
                mProgressBarAuth.setVisibility(View.VISIBLE);
                PlantsLab.logInUser(getActivity(), DEFAULT_USER_ID); //добавляю в бд значение
                startActivity(MainActivity.newIntent(getActivity()));
                getActivity().finish(); //чтобы нельзя было возвратиться из главного окна в окно с авторизацией по кнопке "назад"
            }
        });
        hideKeyboard(getActivity(), container);  //для того чтобы спрятать клавиатуру при касании пальцем по экрану
        return v;
    }
    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

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

    private void signIn(final String email, String password){ //метод ля входа
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //при входе добавляю в бд этого пользователя, при выходе удалю: по такому критерию я проверяю нужно ли выполнять пользователю авторизацию при входе в приложение
                            mProgressBarAuth.setVisibility(View.GONE);
                            amIhaveAccount = true;
                            PlantsLab.logInUser(getActivity(), mUser.getUid()); //добавляю в бд значение
                            startActivity(MainActivity.newIntent(getActivity()));
                            getActivity().finish(); //чтобы нельзя было возвратиться из главного окна в окно с авторизацией по кнопке "назад"
                        }
                        else{
                            mProgressBarAuth.setVisibility(View.GONE);
                            try {
                                if(isEmailValid(email)) //если текст в виде e-mail, то идем дальше
                                    throw task.getException();
                                else{ //иначе напишем, что неправильный формат
                                    RegisterFragment.shakeAnimation(mNotRegisterEmail);
                                    mNotRegisterEmail.setText(R.string.exception_wrong_format_email);
                                }
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                RegisterFragment.shakeAnimation(mNotRegisterEmail);
                                mNotRegisterEmail.setText(R.string.exception_not_register_email);
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                RegisterFragment.shakeAnimation(mWrongPassword);
                                mWrongPassword.setText(R.string.exception_wrong_password);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
    public static void hideKeyboard(final Context context, final ViewGroup container){
        container.setOnClickListener(new View.OnClickListener() { //для того чтобы спрятать клавиатуру при касании пальцем по экрану
            @Override
            public void onClick(View view) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(container.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        });
    }
    public static boolean isEmailValid(String email) { //проверяет что записанный текст в виде e-mail
        return !(email == null || TextUtils.isEmpty(email)) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public void resetPassword(String email){ //метод для отправки сообщения для восстановления пароля
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            dialogResetPassword.hide(); //тот dialog, который вызвал текущий
                            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.reset_password_email_send)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                        }
                                    })
                                    .show();
                        }
                    }
                });
    }
    public static void hideKeyboard(final Context context, final ViewGroup container, final View view){ //прячу клавиатуру при нажатии на какой-то view
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
