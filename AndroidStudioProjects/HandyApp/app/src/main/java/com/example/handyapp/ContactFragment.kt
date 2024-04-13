package com.example.handyapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import java.net.URI
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.telephony.SmsMessage
import android.widget.Toast

class ContactFragment: Fragment() {
    private lateinit var contactText: TextView
    private lateinit var searchContact: Button
    private lateinit var messageButton: Button
    private lateinit var callButton: Button

    private val CONTACT_REQUEST_CODE = 102
    private val TAG = "ContactFragment"
    private var phoneNumber = ""
    private var contactName = "gerge"
    private val READ_CONTACTS_PERM = Manifest.permission.READ_CONTACTS
    private val READ_CONTACTS_REQUEST_CODE = 105
    private val CALL_PHONE_REQUEST_CODE = 104



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.contacts_fragment, container, false)
        contactText = view.findViewById(R.id.contactText)
        searchContact = view.findViewById(R.id.search_contact)
        messageButton = view.findViewById(R.id.message_button)
        callButton = view.findViewById(R.id.call_button)

        callButton.setOnClickListener{callContact()}
        messageButton.setOnClickListener{smsMessage()}
        searchContact.setOnClickListener({searchContactListener()})
        return view
    }

    private fun checkPermissions(): Boolean{
        return requireActivity().checkSelfPermission(READ_CONTACTS_PERM) != PackageManager.PERMISSION_GRANTED
    }

    private fun searchContactListener(){
        val contactIntent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactIntent, CONTACT_REQUEST_CODE)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG,"$resultCode")
        if (requestCode == CONTACT_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Log.i(TAG, "$data")
            val contactUri: Uri? = data?.data
            requestContactsPermission()
            if (!checkPermissions()){
                handleContactQueries(contactUri)

            }
        }



    }
    private fun requestContactsPermission() {
        requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), READ_CONTACTS_REQUEST_CODE)
    }

    fun handleContactQueries(contactUri: Uri?){
        val projectionName = arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER)
        val cursorName = requireActivity().contentResolver.query(contactUri!!, projectionName, null , null, null)
        cursorName?.moveToFirst()
        val contactIdx = cursorName!!.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
        val idIdx = cursorName.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)
        val numIdx = cursorName.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
        contactName =  cursorName.getString(contactIdx)
        contactText.text = contactName
        contactText.visibility = View.VISIBLE
        phoneNumber =  cursorName.getString(numIdx)
        callButton.visibility = View.VISIBLE
        messageButton.visibility = View.VISIBLE

        Log.i(TAG,"")
    }

    private fun callContact(){
        val sharedPrefs = requireContext().getSharedPreferences("contactPreferences", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("lastNumber",phoneNumber).apply()
        requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE_REQUEST_CODE)
    }

    private fun smsMessage(){
        val smsIntent = Intent(Intent.ACTION_SEND).apply {
            data = Uri.parse("sms to:$phoneNumber")
        }
        startActivity(smsIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == CALL_PHONE_REQUEST_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, you can now make the call
                // Proceed with making the call here
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED){
                    val dialIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:$phoneNumber")
                    }
                    startActivity(dialIntent)
                }
            } else {
                // Permission denied
                // You may inform the user that the permission is required to make the call
                Toast.makeText(requireContext(),"Call permission is required to make call", Toast.LENGTH_SHORT).show()
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



}