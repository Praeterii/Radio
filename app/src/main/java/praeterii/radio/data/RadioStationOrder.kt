package praeterii.radio.data

import kotlinx.serialization.Serializable

@Serializable
enum class RadioStationOrder(val value: String) {
    NAME("name"),
    STATIONCOUNT("stationcount"),
    CLICKCOUNT("clickcount");

}