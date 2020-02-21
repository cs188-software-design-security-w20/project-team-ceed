package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class LogInFragmentDirections {
  private LogInFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionLogInFragmentToSignUpFragment() {
    return new ActionOnlyNavDirections(R.id.action_logInFragment_to_signUpFragment);
  }

  @NonNull
  public static NavDirections actionLogInFragmentToMainActivity() {
    return new ActionOnlyNavDirections(R.id.action_logInFragment_to_mainActivity);
  }
}
