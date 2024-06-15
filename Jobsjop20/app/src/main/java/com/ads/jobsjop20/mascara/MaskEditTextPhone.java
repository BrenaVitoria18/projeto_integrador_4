package com.ads.jobsjop20.mascara;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class MaskEditTextPhone {
    public static TextWatcher insert(final EditText editText) {
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = unmask(s.toString());

                // Limitar o número de dígitos para a máscara
                if (str.length() > 11) {
                    str = str.substring(0, 11);
                }

                String maskedStr = "";
                int i = 0;
                String mask = "(##) #####-####";

                if (str.length() > old.length()) {
                    for (char m : mask.toCharArray()) {
                        if (m != '#' && str.length() > old.length()) {
                            maskedStr += m;
                            continue;
                        }
                        try {
                            maskedStr += str.charAt(i);
                        } catch (Exception e) {
                            break;
                        }
                        i++;
                    }
                } else {
                    maskedStr = s.toString();
                }

                isUpdating = true;
                editText.setText(maskedStr);
                editText.setSelection(maskedStr.length());
                old = str;
            }

            @Override
            public void afterTextChanged(Editable s) {}

            private String unmask(String s) {
                return s.replaceAll("[^\\d]", "");
            }
        };
    }
}
