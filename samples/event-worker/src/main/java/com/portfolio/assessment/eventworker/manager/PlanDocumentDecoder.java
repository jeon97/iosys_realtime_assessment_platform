package com.portfolio.assessment.eventworker.manager;

import java.util.List;
import java.util.Map;

public interface PlanDocumentDecoder {
    Map<String, Object> decodeObject(String json);

    List<Map<String, Object>> decodeList(String json);
}

