package com.finalweek10.permission.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import cat.ereza.customactivityoncrash.CustomActivityOnCrash
import com.afollestad.materialdialogs.MaterialDialog
import com.finalweek10.permission.R

class CrashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crash)

        supportActionBar?.let {
            setTitle(R.string.crash_title)
        }

        findViewById<Button>(R.id.btnDetail).setOnClickListener {
            MaterialDialog.Builder(this)
                    .title(R.string.crash_detail)
                    .content(CustomActivityOnCrash
                            .getAllErrorDetailsFromIntent(this, intent))
                    .positiveText(R.string.crash_send)
                    .onPositive { dialog, _ ->
                        val emailIntent = Intent(Intent.ACTION_SENDTO)
                        emailIntent.data = Uri.parse("mailto:")
                        // TODO: Provide your email.
                        emailIntent.putExtra(Intent.EXTRA_EMAIL,
                                arrayOf("developer-email@email.com"))
                        emailIntent.putExtra(Intent.EXTRA_SUBJECT,
                                getString(R.string.crash_email_subject))
                        emailIntent.putExtra(Intent.EXTRA_TEXT,
                                CustomActivityOnCrash
                                        .getAllErrorDetailsFromIntent(this, intent))
                        try {
                            startActivity(Intent.createChooser(emailIntent, "Email"))
                        } catch (e: Exception) {
                        } finally {
                            dialog.dismiss()
                        }
                    }
                    .show()
        }

        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            val config = CustomActivityOnCrash.getConfigFromIntent(intent)
            if (config != null) {
                CustomActivityOnCrash.restartApplication(this, config)
            } else {
                finish()
            }
        }
    }
}
