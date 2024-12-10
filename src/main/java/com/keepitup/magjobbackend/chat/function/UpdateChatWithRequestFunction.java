package com.keepitup.magjobbackend.chat.function;

import com.keepitup.magjobbackend.chat.dto.PatchChatRequest;
import com.keepitup.magjobbackend.chat.entity.Chat;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

@Component
public class UpdateChatWithRequestFunction implements BiFunction<Chat, PatchChatRequest, Chat> {
    @Override
    public Chat apply(Chat chat, PatchChatRequest patchChatRequest) {
        return Chat.builder()
                .id(chat.getId())
                .title(patchChatRequest.getTitle())
                .organization(chat.getOrganization())
                .dateOfCreation(chat.getDateOfCreation())
                .build();
    }
}
