package com.example.desafio3dsm.view

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.desafio3dsm.databinding.ActivityRegisterBinding
import com.example.desafio3dsm.model.Usuario
import com.example.desafio3dsm.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun handleRegister() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        // 1. Validación de campos vacíos (la que ya tenías)
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Todos los campos son requeridos", Toast.LENGTH_SHORT).show()
            return
        }

        // --- ¡¡¡AQUÍ COMIENZA LA VALIDACIÓN DIVINA!!! ---

        // 2. Validación del formato de Email
        if (!email.endsWith("@gmail.com")) {
            binding.etEmail.error = "El correo debe ser de tipo @gmail.com"
            Toast.makeText(this, "El correo debe ser de tipo @gmail.com", Toast.LENGTH_SHORT).show()
            return
        } else {
            // Limpia el error si el formato es correcto
            binding.etEmail.error = null
        }

        // 3. Validación de la contraseña (mínimo 8 caracteres)
        if (password.length < 8) {
            binding.layoutPassword.error = "La contraseña debe tener al menos 8 caracteres"
            Toast.makeText(this, "La contraseña debe tener al menos 8 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        // 4. Validación de contenido de la contraseña (Mayúscula, minúscula, número, especial)
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSpecialChar = password.any { "!@#\$%^&*".contains(it) }

        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecialChar) {
            var errorMessage = "La contraseña debe contener al menos: \n"
            if (!hasUpperCase) errorMessage += "• Una mayúscula\n"
            if (!hasLowerCase) errorMessage += "• Una minúscula\n"
            if (!hasDigit) errorMessage += "• Un número\n"
            if (!hasSpecialChar) errorMessage += "• Un carácter especial (!@#\$%^&*)"

            binding.layoutPassword.error = "La contraseña no cumple los requisitos"
            // Usamos un Toast más largo para que el mensaje completo sea visible
            Toast.makeText(this, errorMessage.trim(), Toast.LENGTH_LONG).show()
            return
        } else {
            // Limpia el error si la contraseña es fuerte
            binding.layoutPassword.error = null
        }

        // 5. Validación de que las contraseñas coincidan
        if (password != confirmPassword) {
            binding.layoutConfirmPassword.error = "Las contraseñas no coinciden"
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return
        } else {
            binding.layoutConfirmPassword.error = null
        }

        // --- FIN DE LA VALIDACIÓN DIVINA ---

        // Si todas las validaciones pasan, procedemos a registrar
        val nuevoUsuario = Usuario(email = email, password = password)

        val call = RetrofitClient.instance.registerUser(nuevoUsuario)
        call.enqueue(object : Callback<Usuario> {
            override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "¡Registro exitoso! Ahora puedes iniciar sesión.", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Error en el registro: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Usuario>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
