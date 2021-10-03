package com.arleux.byart;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.arleux.byart.database.DataBaseScheme;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.io.CharArrayReader;

import static com.arleux.byart.ThePlantsFragment.isEmailValid;

public class SettingsMainFragment extends Fragment {
    private ImageView mSettingsAccountImage;
    private TextView mSettingsAccountLogInSystem;
    private LinearLayout mSettingsAccount;
    private LinearLayout mSettingsSignOut;
    private ObjectAnimator mRegisterButtonAnimator;
    private AnimatorSet animatorSet;
    private boolean mCount; //счетчик при работе с диалоговым окном, чтобы аним происх только при одном клике за цикл
    private boolean mWantedLogIn; //чтобы смотреть что я хочу в данный момент рег-ся или войти
    private View mLogInSystem;
    private Button mRegisterButtonFirst;
    private Button mRegisterButtonSecond;
    private Button mLogInButtonFirst;
    private Button mLogInButtonSecond;
    private LinearLayout mLogInlayout;
    private LinearLayout mPasswordLayout;
    private EditText mPasswordText;
    private EditText mLogInText;
    private TextView mExceptionAlreadyExistEmail;
    private TextView mExceptionNotExistEmail;
    private TextView mExceptionWrongFormatEmail;
    private TextView mExceptionWeakPassword;
    private FirebaseAuth mAuthFromAuthorization;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseUser mUserId;
    private String mUserIdSQLite;
    private String DEFAULT_USER_ID = "default";
    private FirebaseAuth mAuth;
    private boolean amIhaveAccount = false;

    public static Fragment newInstance(){
        return new SettingsMainFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PlantsLab.getIdLogInUser(getActivity()).equals(DEFAULT_USER_ID)) {
            mAuth = FirebaseAuth.getInstance();
        }
        else{
            amIhaveAccount = true;
            mAuth = MainActivity.mAuth;
        }
        mAuthStateListener = new FirebaseAuth.AuthStateListener() { //слушает состояние БД
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                mUserId = firebaseAuth.getCurrentUser(); //представляет сведения о профиле пользователя в базе данных
            }
        };
        mUserIdSQLite = PlantsLab.getIdLogInUser(getActivity()); //тот юзер, что хранится в бд
    }
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings_main, container, false);
        mSettingsAccountImage = v.findViewById(R.id.settings_account_icon);
        mSettingsAccountLogInSystem = v.findViewById(R.id.settings_account_log_in_system);
        mSettingsAccount = v.findViewById(R.id.settings_account);
        mSettingsSignOut = v.findViewById(R.id.settings_account_sign_out);
        //ниже это все для диалогового окна авторизации/регистрации
        mLogInSystem = getLayoutInflater().inflate(R.layout.dialog_main_log_in_system, null);
        mRegisterButtonFirst = mLogInSystem.findViewById(R.id.settings_dialog_register_button_first);
        mRegisterButtonSecond = mLogInSystem.findViewById(R.id.settings_dialog_register_button_second);
        mLogInButtonFirst = mLogInSystem.findViewById(R.id.settings_dialog_log_in_system_button_first);
        mLogInButtonSecond = mLogInSystem.findViewById(R.id.settings_dialog_log_in_system_button_second);
        mLogInlayout = mLogInSystem.findViewById(R.id.dialog_main_register_log_in_layout);
        mPasswordLayout = mLogInSystem.findViewById(R.id.dialog_main_register_password_layout);
        mPasswordText = mLogInSystem.findViewById(R.id.settings_register_password_text);
        mLogInText = mLogInSystem.findViewById(R.id.settings_register_log_in_text);
        mExceptionAlreadyExistEmail = mLogInSystem.findViewById(R.id.exception_already_exist_email_settings);
        mExceptionNotExistEmail = mLogInSystem.findViewById(R.id.exception_not_exist_email_settings);
        mExceptionWrongFormatEmail = mLogInSystem.findViewById(R.id.exception_wrong_format_email_settings);
        mExceptionWeakPassword = mLogInSystem.findViewById(R.id.exception_weak_password_settings);

        mSettingsAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCount = true; //счетчик при работе с диалоговым окном, чтобы аним происх только при одном клике за цикл
                mWantedLogIn = false;
                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.log_in_system)
                        .setPositiveButton(R.string.close, null)
                        .setNeutralButton(R.string.reset_button_back, null)
                        .setView(mLogInSystem)
                        .create();
                dialog.show();
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.INVISIBLE); //сначала кнопка невидима
                //что делает кнопка назад при нажатии
                dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ThePlantsFragment.hideKeyboard(getActivity(), container, dialog.getButton(DialogInterface.BUTTON_NEUTRAL));
                        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.INVISIBLE); //чтобы кнопка в нужный момент исчезала
                        clearTextFieldException();
                        if(!mCount){ //это мой счетчик при работе с диалоговым окном, чтобы аним происх только при одном клике за цикл
                            //два условия, так как кнопка появляется в двух разных местах
                            if (mWantedLogIn){
                                moveButtonAnimation(mLogInButtonFirst, -500);
                                moveButtonAnimation(mLogInButtonSecond, -500);
       //                         appearButtonAnimation(mRegisterButtonFirst);
         //                       appearButtonAnimation(mRegisterButtonSecond);
                                mRegisterButtonFirst.setVisibility(View.VISIBLE);
                                mRegisterButtonSecond.setVisibility(View.VISIBLE);
                                mLogInButtonFirst.setEnabled(true);
                            }
                            else{
                                moveButtonAnimation(mRegisterButtonFirst, -240);
                                moveButtonAnimation(mRegisterButtonSecond, -240);
                                appearButtonAnimation(mLogInButtonFirst);
                                appearButtonAnimation(mLogInButtonSecond);
                                mRegisterButtonSecond.setVisibility(View.GONE);
                                mRegisterButtonSecond.setEnabled(false);
                                mRegisterButtonFirst.setVisibility(View.VISIBLE);
                            }
                            mLogInButtonFirst.setEnabled(true);
                            mLogInButtonSecond.setEnabled(true);
                            mLogInText.setText("");
                            mPasswordText.setText("");
                            mLogInlayout.setVisibility(View.GONE);
                            mPasswordLayout.setVisibility(View.GONE);
                            mCount = true;
                        }
                    }
                });
                //хочу открыть поля для регистрации
                mRegisterButtonFirst.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRegisterButtonFirst.setVisibility(View.GONE);
                        mRegisterButtonSecond.setEnabled(true);
                        mRegisterButtonSecond.setVisibility(View.VISIBLE);
                        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
                        if(mCount){
                            fadeButtonAnimation(mLogInButtonFirst);
                            fadeButtonAnimation(mLogInButtonSecond);
                            mLogInButtonFirst.setEnabled(false);
                            mLogInButtonSecond.setEnabled(false);
                            mLogInlayout.setVisibility(View.VISIBLE);
                            mPasswordLayout.setVisibility(View.VISIBLE);
                            moveButtonAnimation(mRegisterButtonFirst, 240);
                            moveButtonAnimation(mRegisterButtonSecond, 240);
                            mCount = false;
                            mWantedLogIn = false;
                        }
                    }
                });
                //сама регистрация
                    mRegisterButtonSecond.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clearTextFieldException();
                            if(mLogInText.getText().toString().equals("") || mPasswordText.getText().toString().equals("")) { //поля не должны быть пустым
                                Toast.makeText(getActivity(), R.string.enter_exception_null_field, Toast.LENGTH_LONG).show();
                            }
                            else {
                                registerUser(mLogInText.getText().toString(), mPasswordText.getText().toString()); //если эти условия выполнены, то можно попытаться зарегистрировать
                            }
                        }
                    });
                    //вход
                    mLogInButtonFirst.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                if(mCount){
                                    mLogInlayout.setVisibility(View.VISIBLE);
                                    mPasswordLayout.setVisibility(View.VISIBLE);
                                    dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setVisibility(View.VISIBLE);
                                    mRegisterButtonSecond.setVisibility(View.GONE);
                                    mRegisterButtonFirst.setVisibility(View.GONE);

                                    moveButtonAnimation(mLogInButtonFirst, 500);
                                    moveButtonAnimation(mLogInButtonSecond, 500);
                                    mLogInButtonFirst.setEnabled(false);
                                    mWantedLogIn = true;
                                    mCount = false;
                                }
                        }
                    });
                    mLogInButtonSecond.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            clearTextFieldException();
                            if(mLogInText.getText().toString().equals("") || mPasswordText.getText().toString().equals("")) {
                                Toast.makeText(getActivity(), R.string.enter_exception_null_field, Toast.LENGTH_LONG).show();
                            }
                            else {
                                signIn(mLogInText.getText().toString().toLowerCase(), mPasswordText.getText().toString());
                            }
                        }
                    });
                }
        });
        mSettingsSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(getActivity());
                quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //             FirebaseUser user = (FirebaseUser) getIntent().getParcelableExtra(TAG); //получает данные аккаунта из бд, переданные из ThePlantsFragment в эту активность, чтобы выйти из этого акканута
                        PlantsLab.signOutUser(mUserIdSQLite); //при входе добавляю в бд этого пользователя, при выходе удалю: по такому критерию я проверяю нужно ли выполнять пользователю авторизацию при входе в приложение
                        if (mAuth != null){
                            mAuth.signOut(); //выход из аккаунта: такой способ через public static плохой, но по-другому как хз
                        }
                        startActivity(ThePlantsActivity.newIntent(getActivity())); //переходит к авторизации
                    }
                });
                quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                if (amIhaveAccount)
                    quitDialog.setTitle(R.string.sign_out_have_account);
                else
                    quitDialog.setTitle(R.string.sign_out_not_have_account);
                quitDialog.show();
            }
        });
        return v;
    }
    @Override
    public void onResume(){
        super.onResume();
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null)
            mAuth.addAuthStateListener(mAuthStateListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
    public void moveButtonAnimation(View view, int deltaY){ //плавное движение кнопки регистрации
        mRegisterButtonAnimator = ObjectAnimator.ofFloat(view, "y",view.getY(), view.getY() + deltaY)
                .setDuration(370);
        animatorSet = new AnimatorSet();
        animatorSet.play(mRegisterButtonAnimator);
        animatorSet.start();
    }
    public void fadeButtonAnimation(final View view){ //плавное исчезновение кнопки войти
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.fade);
        view.startAnimation(animation);
    }
    public void appearButtonAnimation(final View view){ //плавное появление кнопки войти
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.appear);
        view.startAnimation(animation);
    }
    private void registerUser(final String email, final String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification(); //отправляю на этот email подтверждение почты
                            final AlertDialog dialogOnFinishButton = new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.confirm_email_message)
                                    .setPositiveButton(R.string.reset_button_next, null)
                                    .setNeutralButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            mUserId.delete();
                                            dialogInterface.cancel(); //переходит назад
                                            mLogInText.setText("");
                                            mPasswordText.setText("");
                                        }
                                    })
                                    .setCancelable(false)
                                    .create();
                            dialogOnFinishButton.show();
                            //сам реализую лисенер, так как для меня важно, что не при любом клике будет переход с авториции, а только если почта подтверждена
                            dialogOnFinishButton.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(mUserId != null){
                                        mUserId.reload(); //обнови информацию о нём
                                        if(mUserId.isEmailVerified()) { //если этот user подтвердил email, то перейдём к авторизации
                                            dialogOnFinishButton.hide();
                                            final boolean amIhaveAccount = true;
                                            final AlertDialog dialogOnFinishButton = new AlertDialog.Builder(getActivity())
                                                    .setTitle(R.string.register_congratulations)
                                                    .setCancelable(false)
                                                    .setNegativeButton(R.string.enter_login_button_text, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            signInWithEmailAndPassword(email, password);

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
                                RegisterFragment.shakeAnimation(mExceptionWeakPassword);
                                mExceptionWeakPassword.setText(R.string.exception_weak_password);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                RegisterFragment.shakeAnimation(mExceptionWrongFormatEmail);
                                mExceptionWrongFormatEmail.setText(R.string.exception_wrong_format_email);
                            } catch (FirebaseAuthUserCollisionException e) {
                                RegisterFragment.shakeAnimation(mExceptionAlreadyExistEmail);
                                mExceptionAlreadyExistEmail.setText(R.string.exception_already_exist_email);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
    private void signIn(final String email, String password){ //метод ля входа
        if (amIhaveAccount){//если уже вошел в аккаунт, то проверь не хочу ли войти в тот же аккаунт
            if(mUserId.getEmail().equals(email)){
                Toast.makeText(getActivity(), R.string.you_already_sign_in_account, Toast.LENGTH_LONG).show();
            }
            else {
                signInWithEmailAndPassword(email, password);
            }
        }
        else
            signInWithEmailAndPassword(email, password);
    }
    private void signInWithEmailAndPassword(final String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //при входе добавляю в бд этого пользователя, при выходе удалю: по такому критерию я проверяю нужно ли выполнять пользователю авторизацию при входе в приложение
                            boolean amIhaveAccount = true; //это значит что он вошел в аккаунт
                            PlantsLab.signOutUser(mUserIdSQLite); //я захожу в акк, следовательно нужно удалить из бд пользователя, который без авторизации
                            PlantsLab.logInUser(getActivity(), mUserId.getUid()); //добавляю в бд значение
                            ThePlantsFragment.mAuth = mAuth; //меняю значение БД для данного пользователя в классе авторизации, чтобы также изменилось значение и в MainActivity, которое определяется им
                            startActivity(MainActivity.newIntent(getActivity()));
                        }
                        else{
                            try {
                                if(isEmailValid(email)) //если текст в виде e-mail, то идем дальше
                                    throw task.getException();
                                else{ //иначе напишем, что неправильный формат
                                    RegisterFragment.shakeAnimation(mExceptionWrongFormatEmail);
                                    mExceptionWrongFormatEmail.setText(R.string.exception_wrong_format_email);
                                }
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                RegisterFragment.shakeAnimation(mExceptionNotExistEmail);
                                mExceptionNotExistEmail.setText(R.string.exception_not_register_email);
                            }
                            catch (FirebaseAuthInvalidCredentialsException e){
                                RegisterFragment.shakeAnimation(mExceptionWeakPassword);
                                mExceptionWeakPassword.setText(R.string.exception_wrong_password);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }
    private void clearTextFieldException(){
        mExceptionAlreadyExistEmail.setText("");
        mExceptionNotExistEmail.setText("");
        mExceptionWeakPassword.setText("");
        mExceptionWrongFormatEmail.setText("");
    }
}
