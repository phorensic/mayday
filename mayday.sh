#!/system/bin/sh
tsu
settings put global airplane_mode_on 1
am broadcast -a android.intent.action.AIRPLANE_MODE
settings put global airplane_mode_on 0
am broadcast -a android.intent.action.AIRPLANE_MODE