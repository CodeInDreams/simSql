package sim.sql.dto;

import java.util.UUID;

public class ExampleDTO {
    private final String strValue;
    private final Integer intValue;
    private final long longValue;
    private final OtherTypeDTO otherTypeDTO = new OtherTypeDTO();

    public ExampleDTO(String strValue, Integer intValue, Long longValue) {
        this.strValue = strValue;
        this.intValue = intValue;
        this.longValue = longValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public Long getLongValue() {
        return longValue;
    }

    public OtherTypeDTO getOtherTypeDTO() {
        return otherTypeDTO;
    }

    @Override
    public String toString() {
        return "ExampleDTO{" +
                "strValue='" + strValue + '\'' +
                ", intValue=" + intValue +
                ", LongValue=" + longValue +
                ", otherTypeDTO=" + otherTypeDTO +
                '}';
    }

    public static class OtherTypeDTO {
        private final String randomStr = UUID.randomUUID().toString();

        public String getRandomStr() {
            return randomStr;
        }

        @Override
        public String toString() {
            return "OtherTypeDTO{" +
                    "randomStr='" + randomStr + '\'' +
                    '}';
        }
    }
}
