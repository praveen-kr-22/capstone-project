use armorcode;
SET SQL_SAFE_UPDATES = 0;
delete from Runbook;
delete from Ticket;
CREATE TABLE Runbook (
    id INT AUTO_INCREMENT PRIMARY KEY,
    input VARCHAR(255),
    task JSON
);


INSERT INTO Runbook (input, task) VALUES (
    'new findings',
    '{
        "filter": [
            {
                "type": "toolName",
                "values": [
                    "dependabot",
                    "code scan",
                    "secret scan"
                ]
            },
            {
                "type": "securityLevel",
                "values": [
                    "Critical",
                    "High",
                    "Medium",
                    "Low",
                    "Info",
                    "False Positive"
                ]
            },
            {
                "type": "productName",
                "values": [
                    "Demo App",
                    "Demo App 1",
                    "Demo App 2"
                ]
            }
        ],
        "action": [
            "Create new jira ticket",
            "Send email"
        ]
    }'
);







