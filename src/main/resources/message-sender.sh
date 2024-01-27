export ZEEBE_CLIENT_ID=library-poc
export ZEEBE_CLIENT_SECRET=
export ZEEBE_INSECURE_CONNECTION=true
export ZEEBE_TOKEN_AUDIENCE=zeebe-api
export ZEEBE_AUTHORIZATION_SERVER_URL=http://localhost:18080/auth/realms/camunda-platform/protocol/openid-connect/token

# "Cancel reservation 메세지 보내기"
zbctl --insecure publish message "Message_cancel-reservation" --correlationKey "isbn1234_msjo" --variables "{\"from\" : \"zbctl\" }"

# "Order cancelled 메세지 보내기"
zbctl --insecure publish message "Message_order-cancelled" --correlationKey "isbn1234_msjo" --variables "{\"from\" : \"zbctl\" }"

# "Book returned 메세지 보내기"
zbctl --insecure publish message "Message_book-returned" --correlationKey "isbn1234_msjo" --variables "{\"from\" : \"zbctl\" }"

# "Manage Book Order Process에서 3일 기다리는 동안 Cancel Order 메세지 보내는 명령"
zbctl --insecure publish message "Message_Cancel-order" --correlationKey "isbn1234_msjo"

# "Manage Book Order Process에서 3일 기다리는 동안 Pickup Book 메세지 보내는 명령"
zbctl --insecure publish message "Message_Pick-up-book" --correlationKey "isbn1234_msjo"

# "Manage Borrowed Bood Process에서 Renewal Request 보내는 명령"
zbctl --insecure publish message "Message_Renew-request" --correlationKey "msjo_isbn1234"

# "Manage Borrowed Bood Process에서 Renewal Request 보내는 명령"
zbctl --insecure publish message "Message_Return-book" --correlationKey "msjo_isbn1234"