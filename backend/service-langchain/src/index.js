require('dotenv').config({ path: require('path').resolve(__dirname, '../../../.env') });

const { ChatGoogleGenerativeAI } = require("@langchain/google-genai");
const { PromptTemplate } = require("@langchain/core/prompts");
const Twilio = require("twilio");

const args = process.argv.slice(2);
if (args.length < 3) {
    console.error("Error: Se esperaban 3 argumentos: userName, latitude, longitude.");
    process.exit(1);
}
const [userName, latitude, longitude] = args;

const model = new ChatGoogleGenerativeAI({
    apiKey: process.env.GOOGLE_API_KEY,
    modelName: "gemini-2.5-flash",
});

console.log("--- VERIFICANDO VARIABLES DE ENTORNO ---");
console.log("SID de la cuenta:", process.env.TWILIO_ACCOUNT_SID);
console.log("Número de teléfono:", process.env.TWILIO_PHONE_NUMBER);
console.log("------------------------------------");

const twilioClient = new Twilio(process.env.TWILIO_ACCOUNT_SID, process.env.TWILIO_AUTH_TOKEN);

async function createEmergencyMessage() {
    console.log("Generando mensaje de emergencia con LangChain...");

    const template = new PromptTemplate({
        template: `Eres un asistente de emergencias. Genera un mensaje de texto corto, claro y urgente para notificar al contacto de emergencia sobre una posible caída de un usuario.\n\nEl mensaje debe incluir:\n- El nombre del usuario.\n- Una advertencia clara sobre una posible caída.\n- La ubicación aproximada (latitud y longitud).\n- Una solicitud para que el contacto intente comunicarse con el usuario.\n\nNombre del usuario: {userName}\nLatitud: {latitude}\nLongitud: {longitude}\n\nMensaje de emergencia:`,
        inputVariables: ["userName", "latitude", "longitude"],
    });

    const prompt = await template.format({ userName, latitude, longitude });
    const response = await model.invoke(prompt);

    console.log(`Mensaje generado: ${response.content}`);
    return response.content;
}

async function sendSms(message) {
    const from = process.env.TWILIO_PHONE_NUMBER;
    const to = process.env.EMERGENCY_CONTACT_NUMBER;

    if (!from || !to) {
        console.error("Error: El número de Twilio o el número del contacto no están definidos en .env");
        return;
    }

    console.log(`Enviando SMS desde ${from} hacia ${to}...`);

    try {
        const twilioResponse = await twilioClient.messages.create({
            body: message,
            from: `whatsapp:${from}`,
            to: `whatsapp:${to}`,
        });
        console.log("SMS enviado con éxito. SID: ", twilioResponse.sid);
    } catch (error) {
        console.error("Error al enviar el SMS con Twilio:", error);
    }
}

async function main() {
    try {
        const message = await createEmergencyMessage();
        await sendSms(message);
    } catch (error) {
        console.error("Ocurrió un error en el flujo principal:", error);
    }
}

main();
