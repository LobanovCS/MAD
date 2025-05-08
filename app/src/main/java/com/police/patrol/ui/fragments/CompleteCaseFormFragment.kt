package com.police.patrol.ui.fragments

import CompleteCaseRequest
import CompleteCaseResponse
import RetrofitClient
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.police.patrol.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CompleteCaseFormFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_complete_case_form, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etCaseId = view.findViewById<EditText>(R.id.case_id)
        val etActions = view.findViewById<EditText>(R.id.et_actions_taken)
        val etName = view.findViewById<EditText>(R.id.et_offender_name)
        val etVerdict = view.findViewById<EditText>(R.id.et_verdict)
        val btnSubmit = view.findViewById<Button>(R.id.btn_submit_complete_case)

        btnSubmit.setOnClickListener {
            val caseIdText = etCaseId.text.toString().trim()
            val actions = etActions.text.toString().trim()
            val name = etName.text.toString().trim()
            val verdict = etVerdict.text.toString().trim()

            if (caseIdText.isEmpty() || actions.isEmpty()) {
                Toast.makeText(requireContext(), "Введите номер дела и действия", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val caseId = try {
                caseIdText.toInt()
            } catch (e: NumberFormatException) {
                Toast.makeText(requireContext(), "Неверный формат ID дела", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CompleteCaseRequest(
                case_id = caseId,
                actions_taken = actions,
                violator_name = if (name.isNotEmpty()) name else null,
                verdict = if (verdict.isNotEmpty()) verdict else null
            )

            RetrofitClient.apiService.completeCase(request)
                .enqueue(object : Callback<CompleteCaseResponse> {
                    override fun onResponse(
                        call: Call<CompleteCaseResponse>, response: Response<CompleteCaseResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            Toast.makeText(requireContext(), result?.message, Toast.LENGTH_SHORT).show()
                            if (result?.success == true) {
                                findNavController().popBackStack()
                            }
                        } else {
                            Log.e("API Error", "Response: ${response.errorBody()?.string()}")
                            Toast.makeText(requireContext(), "Ошибка сервера", Toast.LENGTH_SHORT).show()
                        }
                    }


                    override fun onFailure(call: Call<CompleteCaseResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), "Сбой: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
