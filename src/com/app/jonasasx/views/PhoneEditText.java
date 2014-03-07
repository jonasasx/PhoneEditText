/*
 * PhoneEditText
 *
 * v1.0
 *
 * 2014-02-07
 * 
 * This file is copyrighted in GPLv2
 */
package com.app.taxeriki.views;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

public class PhoneEditText extends EditText implements TextWatcher {
	private static final String LOG_TAG = "PET";
	private String _mask;
	private String _regexp;
	private String _replace;
	private String _raw;
	private int _maskStart;
	private int _startSelection;
	private int _insertCount;
	private boolean _changing = false;

	public PhoneEditText(Context context) {
		super(context);
		init();
	}

	public PhoneEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PhoneEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	protected void init() {
		_mask = "+7 (###) ###-##-##";
		_regexp = "\\D+";
		_replace = "#";
		_raw = "";
		_maskStart = _mask.indexOf(_replace);
		setText(_mask.replace(_replace, " "));
		addTextChangedListener(this);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		Log.v(LOG_TAG, "beforeTextChanged(" + s + "," + Integer.toString(start) + ", " + Integer.toString(count) + ", " + Integer.toString(after) + ")");
		_startSelection = start;
		_insertCount = after;
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		Log.v(LOG_TAG, "onTextChanged(" + s + "," + Integer.toString(start) + ", " + Integer.toString(before) + ", " + Integer.toString(count) + ")");
	}

	@Override
	public void afterTextChanged(Editable s) {
		Log.v(LOG_TAG, "afterTextChanged(" + s + ")");
		makeRaw(s.toString());
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		if (_mask == null || _replace == null || getText().toString().length() == 0)
			return;
		Log.v(LOG_TAG, "onSelectionChanged(" + Integer.toString(selStart) + ", " + Integer.toString(selEnd) + ")");
		int minSel = _mask.indexOf(_replace, selStart);

		if (minSel == -1)
			minSel = _mask.length();
		minSel = Math.min(minSel, _mask.indexOf(_replace, getIndexOf(_mask, _replace, _raw.length())));
		if (minSel == -1)
			minSel = _mask.length();
		minSel = Math.max(minSel, _maskStart);

		int maxSel = minSel;

		setSelection(minSel, maxSel);
		super.onSelectionChanged(minSel, maxSel);
	}

	private void makeRaw(CharSequence source) {
		if (_changing)
			return;
		_changing = true;
		int maxLength = substr_count(_mask, _replace);

		int m = _startSelection + _insertCount;
		String subRaw = "";
		for (int i = _startSelection; i < m; i++) {
			subRaw += source.toString().substring(i, i + 1);
		}
		subRaw = subRaw.replaceAll(_regexp, "");
		int d = 0;
		int l = _raw.length();
		int a = substr_count(_mask, _replace, _startSelection) - 1;
		String first = l >= a && a > -1 ? _raw.substring(0, a) : "";
		if (_startSelection == m && a > -1)
			d = 1;
		String last = l > a + d && a > -1 ? _raw.substring(a + d, l) : "";
		String newRaw = first + subRaw + last;
		if (newRaw.length() <= maxLength) {
			_raw = newRaw;
			_raw = _raw.substring(0, Math.min(_raw.length(), maxLength));
		}
		setText(getMaskedText());
		_changing = false;
	}

	private String getMaskedText() {
		String text = _mask.toString();
		int m = text.length();
		int l = _replace.length();
		int j = 0;
		for (int i = 0; i < m; i++) {
			if (_mask.substring(i, i + l).equals(_replace)) {
				String first = text.substring(0, i);
				String last = text.substring(i + l, m);
				if (_raw.length() > j)
					text = first + _raw.substring(j, j + 1) + last;
				else
					text = first + " " + last;
				j++;
			}
		}
		return text;
	}

	private int substr_count(String string, String subString) {
		int c = 0;
		int lastIndex = 0;
		while (lastIndex != -1) {
			lastIndex = string.indexOf(subString, lastIndex);
			if (lastIndex != -1) {
				c++;
				lastIndex += subString.length();
			}
		}
		return c;
	}

	private int substr_count(String string, String subString, int r) {
		int c = 0;
		int lastIndex = 0;
		while (lastIndex != -1 && lastIndex <= r) {
			lastIndex = string.indexOf(subString, lastIndex);
			if (lastIndex != -1 && lastIndex <= r) {
				c++;
				lastIndex += subString.length();
			}
		}
		return c;
	}

	private int getIndexOf(String string, String subString, int l) {
		int c = 0;
		int lastIndex = 0;
		if (l == 0)
			return 0;
		while (lastIndex != -1 && c <= l) {
			lastIndex = string.indexOf(subString, lastIndex);
			if (lastIndex != -1 && c <= l) {
				c++;
				lastIndex += subString.length();
				if (c >= l)
					break;
			}
		}
		return lastIndex;
	}

	public String getRawText() {
		return _raw;
	}
}

