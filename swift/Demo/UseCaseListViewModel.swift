import JustTrackSDK
import SwiftUI

final class UseCaseListViewModel: ObservableObject {
    @Published var useCases: [UseCase] = []

    @Published var showAlert = false
    @Published var alertTitle = ""
    @Published var alertMessage = ""

    @Published var isTrying = false

    private let sdk: JustTrackSdk

    init() {
        sdk = try! JustTrackSdkBuilder(
            apiToken: "your_api_token"
        )
        .set(manualStart: true)
        .build()

        generateUseCases()
    }

    private func generateUseCases() {
        useCases =
        {
            if sdk.isRunning() {
                [
                    UseCase(
                        name: "Stop the SDK",
                        description: "Stop the SDK to prevent tracking the user and sending any user data to the justtrack servers.",
                        onTry: { [weak self, sdk] in
                            sdk.stop()
                            self?.generateUseCases()
                        }
                    )
                ]
            } else {
                [
                    UseCase(
                        name: "Start the SDK",
                        description: "Start the SDK to make it operational. You can only call most of the SDK methods after it's started.",
                        onTry: { [weak self, sdk] in
                            sdk.start()
                            self?.generateUseCases()
                        }
                    )
                ]
            }
        }()
        +
        [
            UseCase(
                name: "Anonymize user installs, events, and sessions",
                description: "By calling the anonymize() method, you can instruct the justtrack backend to delete identifying information about the user.",
                onTry: { [weak self, sdk] in
                    sdk.anonymize().observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The user data has been successfully anonymized.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Publish a user event with a name, value, and unit",
                description: "You can instantiate a UserEvent with a name, value, and unit. The value can be any number, and the unit can be either count, milliseconds, or seconds.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let event = AppEvent(
                        name: "value_unit_event",
                        value: 100,
                        unit: .count
                    )

                    // You can also validate the event before publishing it to be sure that its name and dimensions have the proper format.
                    do {
                        try event.validate()
                    } catch {
                        self?.displayAlert(title: "Error", message: error.localizedDescription)
                        return
                    }

                    sdk.publish(event: event).observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The event was successfully published.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Publish a user event with a name and a money value",
                description: "You can instantiate a UserEvent with a name and a money value. The value can be any number, and the currency can be any string.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let event = AppEvent(
                        name: "money_event",
                        money: Money(value: 100, currency: "EUR")
                    )

                    sdk.publish(event: event).observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The event was successfully published.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Publish a user event with dimensions",
                description: "You can add dimensions, which are key-value pairs of strings, to any user event.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let event = AppEvent(
                        name: "money_event",
                        money: Money(value: 100, currency: "EUR")
                    )
                        .add(dimension: "dimension_1", value: "value_1")
                        .add(dimension: "dimension_2", value: "value_2")

                    sdk.publish(event: event).observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The event was successfully published.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Publish a predefined event",
                description: "You can also send a predefined event. The SDK provides a list of predefined events, which you can find in the documentation." /* at https://docs.justtrack.io/sdk/5.0.x/overview/predefined-events */,
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let event = JtLoginEvent(jtAction: "login")

                    sdk.publish(event: event).observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The event was successfully published.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Set a custom user identifier",
                description: "You can set your own user ID to use it instead of the one provided by the SDK.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let userId = "custom_user_id"

                    sdk.set(userId: userId).observe { result in
                        switch result {
                        case .success:
                            self?.displayAlert(title: "Success", message: "The user identifier was successfully set.")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Get a test group identifier",
                description: "You can get the test group identifier of the current user, which is an integer from the set [1, 2, 3].",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    if let testGroupId = sdk.testGroupId {
                        self?.displayAlert(title: "Success", message: "The test group identifier is \(testGroupId).")
                    } else {
                        self?.displayAlert(title: "Error", message: "The test group identifier is not available.")
                    }
                }
            ),
            UseCase(
                name: "Get an attribution",
                description: "You can get attribution data for the current user.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    sdk.attribution.observe { result in
                        switch result {
                        case let .success(attributionResponse):
                            let justtrackUserId = attributionResponse.justtrackUserId
                            let campaign = attributionResponse.campaign
                            self?.displayAlert(title: "Success", message: "The user identifier is \(justtrackUserId) and the campaign is \(campaign).")
                        case let .failure(error):
                            self?.displayAlert(title: "Error", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Get advertiser information",
                description: "You can get the advertiser identifier and the limited ad tracking status for the current user.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    sdk.getAdvertiserIdInfo().observe { result in
                        switch result {
                        case let .success(info):
                            let advertiserId = info.advertiserId
                            let limitedAdTracking = info.limitedAdTracking
                            self?.displayAlert(title: "Success", message: "The advertiser identifier is \(advertiserId ?? "nil") and the limited ad tracking status is \(limitedAdTracking).")
                        case let .failure(error):
                            self?.displayAlert(title: "Failure", message: error.localizedDescription)
                        }
                    }
                }
            ),
            UseCase(
                name: "Forward an ad impression",
                description: "You can forward an ad impression to the justtrack backend.",
                onTry: { [weak self, sdk] in
                    self?.isTrying = true

                    let adImpression = AdImpression(unit: "impression_unit", sdkName: "sdk_name")
                        .set(bundleId: "bundle_id")
                        .set(instanceName: "instance_name")
                        .set(network: "network")
                        .set(placement: "placement")
                        .set(revenue: Money(value: 100, currency: "EUR"))
                        .set(segmentName: "segment_name")
                        .set(testGroup: "test_group")

                    switch sdk.forward(adImpression: adImpression) {
                    case .success:
                        self?.displayAlert(title: "Success", message: "The ad impression was forwarded.")
                    case let .failure(error):
                        self?.displayAlert(title: "Failure", message: "The ad impression wasn't forwarded. Error: \(error.localizedDescription)")
                    }
                }
            ),
        ]
    }

    private func displayAlert(title: String, message: String) {
        DispatchQueue.main.async {
            self.isTrying = false
            self.alertTitle = title
            self.alertMessage = message
            self.showAlert = true
        }
    }
}
