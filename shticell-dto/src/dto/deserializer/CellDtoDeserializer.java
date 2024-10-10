package dto.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import dto.CellDto;
import dto.CoordinateDto;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

public class CellDtoDeserializer implements JsonDeserializer<CellDto> {

    @Override
    public CellDto deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        CellDto cellDto = new CellDto();
        cellDto.coordinate = context.deserialize(json.getAsJsonObject().get("coordinate").getAsJsonObject(), CoordinateDto.class);
        cellDto.version = json.getAsJsonObject().get("version").getAsInt();
        cellDto.originalValue = json.getAsJsonObject().get("originalValue").getAsString();
        cellDto.effectiveValue = json.getAsJsonObject().get("effectiveValue").getAsString();

        Set<CellDto> SetInfluence = new HashSet<>();

        json.getAsJsonObject().get("influenceFrom").getAsJsonArray()
                .forEach(jsonElement -> {
                    CellDto influencer = deserialize(jsonElement.getAsJsonObject(),typeOfT,context);
                    SetInfluence.add(influencer);
                });

        SetInfluence.forEach(cellDto1 -> cellDto1.influenceOn.add(cellDto));

        return null;
    }
}
