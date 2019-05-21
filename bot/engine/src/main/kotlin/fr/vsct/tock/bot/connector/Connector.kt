/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.vsct.tock.bot.connector

import fr.vsct.tock.bot.connector.media.MediaMessage
import fr.vsct.tock.bot.definition.IntentAware
import fr.vsct.tock.bot.definition.StoryHandlerDefinition
import fr.vsct.tock.bot.definition.StoryStep
import fr.vsct.tock.bot.engine.BotBus
import fr.vsct.tock.bot.engine.ConnectorController
import fr.vsct.tock.bot.engine.event.Event
import fr.vsct.tock.bot.engine.user.PlayerId
import fr.vsct.tock.bot.engine.user.UserPreferences

/**
 * A connector connects bots to users via a dedicated interface (like Messenger, Google Assistant, Slack... ).
 *
 * There is one Connector for each user front-end application.
 * See [fr.vsct.tock.bot.connector.messenger.MessengerConnector] or [fr.vsct.tock.bot.connector.ga.GAConnector] for examples of [Connector] implementations.
 */
interface Connector {

    /**
     * The type of the connector.
     */
    val connectorType: ConnectorType

    /**
     * Registers the connector for the specified controller.
     */
    fun register(controller: ConnectorController)

    /**
     * Unregisters the connector.
     */
    fun unregister(controller: ConnectorController) {
        controller.unregisterServices()
    }

    /**
     * Send an event with this connector for the specified delay.
     *
     * @param event the event to send
     * @param callback the initial connector callback
     * @param delayInMs the optional delay
     */
    fun send(event: Event, callback: ConnectorCallback, delayInMs: Long = 0)

    /**
     * Sends a notification to the connector.
     * A [BotBus] is created and the corresponding story is called.
     *
     * @param applicationId the configuration connector id
     * @param recipientId the recipient identifier
     * @param intent the notification intent
     * @param step the optional step target
     * @param parameters the optional parameters
     */
    fun notify(
        controller: ConnectorController,
        recipientId: PlayerId,
        intent: IntentAware,
        step: StoryStep<out StoryHandlerDefinition>? = null,
        parameters: Map<String, String> = emptyMap()
    ): Unit =
        throw UnsupportedOperationException("Connector $connectorType does not support notification")

    /**
     * Load user preferences - default implementation returns null.
     */
    fun loadProfile(callback: ConnectorCallback, userId: PlayerId): UserPreferences? = null

    /**
     * Refresh user preferences - default implementation returns null.
     * Only not null values are taken into account.
     */
    fun refreshProfile(callback: ConnectorCallback, userId: PlayerId): UserPreferences? = null

    /**
     * Returns a [ConnectorMessage] with the specified list of suggestions.
     * If the connector does not support suggestions, returns null.
     */
    fun addSuggestions(text: CharSequence, suggestions: List<CharSequence>): ConnectorMessage? = null

    /**
     * Updates a [ConnectorMessage] with the specified list of suggestions.
     * Default returns [message] unmodified.
     */
    fun addSuggestions(message: ConnectorMessage, suggestions: List<CharSequence>): ConnectorMessage = message

    /**
     * Transforms a [MediaMessage] to a [ConnectorMessage].
     * If returns null, the transformation is not supported.
     * Default returns null.
     */
    fun toConnectorMessage(message: MediaMessage): ConnectorMessage? = null

}