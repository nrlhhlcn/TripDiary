// Generated by view binder compiler. Do not edit!
package com.mutu.tripdiary.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.mutu.tripdiary.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityRegisterBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final EditText email;

  @NonNull
  public final ConstraintLayout main;

  @NonNull
  public final EditText name;

  @NonNull
  public final EditText password;

  @NonNull
  public final Button registterClick;

  @NonNull
  public final EditText repeatPassword;

  @NonNull
  public final EditText surname;

  @NonNull
  public final TextView textView2;

  @NonNull
  public final EditText username;

  private ActivityRegisterBinding(@NonNull ConstraintLayout rootView, @NonNull EditText email,
      @NonNull ConstraintLayout main, @NonNull EditText name, @NonNull EditText password,
      @NonNull Button registterClick, @NonNull EditText repeatPassword, @NonNull EditText surname,
      @NonNull TextView textView2, @NonNull EditText username) {
    this.rootView = rootView;
    this.email = email;
    this.main = main;
    this.name = name;
    this.password = password;
    this.registterClick = registterClick;
    this.repeatPassword = repeatPassword;
    this.surname = surname;
    this.textView2 = textView2;
    this.username = username;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityRegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.email;
      EditText email = ViewBindings.findChildViewById(rootView, id);
      if (email == null) {
        break missingId;
      }

      ConstraintLayout main = (ConstraintLayout) rootView;

      id = R.id.name;
      EditText name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.password;
      EditText password = ViewBindings.findChildViewById(rootView, id);
      if (password == null) {
        break missingId;
      }

      id = R.id.registterClick;
      Button registterClick = ViewBindings.findChildViewById(rootView, id);
      if (registterClick == null) {
        break missingId;
      }

      id = R.id.repeatPassword;
      EditText repeatPassword = ViewBindings.findChildViewById(rootView, id);
      if (repeatPassword == null) {
        break missingId;
      }

      id = R.id.surname;
      EditText surname = ViewBindings.findChildViewById(rootView, id);
      if (surname == null) {
        break missingId;
      }

      id = R.id.textView2;
      TextView textView2 = ViewBindings.findChildViewById(rootView, id);
      if (textView2 == null) {
        break missingId;
      }

      id = R.id.username;
      EditText username = ViewBindings.findChildViewById(rootView, id);
      if (username == null) {
        break missingId;
      }

      return new ActivityRegisterBinding((ConstraintLayout) rootView, email, main, name, password,
          registterClick, repeatPassword, surname, textView2, username);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
