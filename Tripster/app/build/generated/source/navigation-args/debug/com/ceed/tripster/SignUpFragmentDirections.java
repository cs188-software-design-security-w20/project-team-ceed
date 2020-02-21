package com.ceed.tripster;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;

public class SignUpFragmentDirections {
  private SignUpFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionSignUpFragmentToLogInFragment() {
    return new ActionOnlyNavDirections(R.id.action_signUpFragment_to_logInFragment);
  }

  @NonNull
  public static NavDirections actionSignUpFragmentToMainActivity() {
    return new ActionOnlyNavDirections(R.id.action_signUpFragment_to_mainActivity);
  }
}
