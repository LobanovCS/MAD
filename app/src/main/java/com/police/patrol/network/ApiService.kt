import com.police.patrol.data.model.ActiveCase
import com.police.patrol.data.model.Patrol
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

data class RegisterRequest(
    val officer_names: String,
    val vehicle_number: String?
)

data class ToggleRequest(
    val officer_names: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

data class ToggleResponse(
    val success: Boolean,
    val new_status: Int,
    val patrol_id: Int? = null
)

data class CompleteCaseRequest(
    val case_id: Int,
    val actions_taken: String,
    val violator_name: String?,
    val verdict: String?
)

data class CompleteCaseResponse(
    val success: Boolean,
    val message: String
)

interface ApiService {

    @POST("register_patrol.php")
    fun registerPatrol(@Body request: RegisterRequest): Call<Map<String, Any>>

    @POST("toggle_status.php")
    fun toggleStatus(@Body request: ToggleRequest): Call<ToggleResponse>

    @GET("get_patrol_units.php")
    fun getAllPatrols(): Call<List<Patrol>>

    @POST("update_location.php")
    fun updateLocation(@Body request: ToggleRequest): Call<Map<String, Any>>

    @GET("get_active_cases.php")
    fun getActiveCases(): Call<List<ActiveCase>>

    @FormUrlEncoded
    @POST("assign_case.php")
    fun assignCase(
        @Field("case_id") caseId: Int,
        @Field("patrol_id") patrolId: Int
    ): Call<Map<String, Any>>

    @POST("complete_case.php")
    fun completeCase(@Body request: CompleteCaseRequest): Call<CompleteCaseResponse>


}